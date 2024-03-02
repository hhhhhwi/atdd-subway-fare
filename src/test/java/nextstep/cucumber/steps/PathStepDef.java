package nextstep.cucumber.steps;

import io.cucumber.java8.En;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.acceptance.line.LineApiRequester;
import nextstep.subway.acceptance.path.PathApiRequester;
import nextstep.subway.acceptance.section.SectionApiRequester;
import nextstep.subway.acceptance.station.StationApiRequester;
import nextstep.subway.line.dto.LineCreateRequest;
import nextstep.subway.line.dto.SectionCreateRequest;
import nextstep.subway.station.dto.StationResponse;
import nextstep.utils.JsonPathUtil;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class PathStepDef implements En {

    Long 교대역id;
    Long 양재역id;
    Long 남부터미널역id;
    Long 삼호선id;

    ExtractableResponse<Response> response;

    public PathStepDef() {
        Given("지하철노선을 생성하고", () -> {
            교대역id = JsonPathUtil.getId(StationApiRequester.createStationApiCall("교대역"));
            양재역id = JsonPathUtil.getId(StationApiRequester.createStationApiCall("양재역"));
            남부터미널역id = JsonPathUtil.getId(StationApiRequester.createStationApiCall("남부터미널역"));

            LineCreateRequest 삼호선 = new LineCreateRequest("3호선", "orange", 교대역id, 남부터미널역id, 2);
            삼호선id = JsonPathUtil.getId(LineApiRequester.createLineApiCall(삼호선));

            SectionCreateRequest 남부터미널양재역 = new SectionCreateRequest(남부터미널역id, 양재역id, 3);
            SectionApiRequester.generateSection(남부터미널양재역, 삼호선id);
        });
        When("출발지와 도착지의 경로를 조회하면", () -> {
            response = PathApiRequester.getPath(교대역id, 양재역id);
        });

        Then("출발지와 도착지의 경로가 조회된다", () -> {
            List<Long> ids = response.jsonPath().getList("stations", StationResponse.class)
                    .stream().map(StationResponse::getId).collect(Collectors.toList());
            int distance = response.jsonPath().getInt("distance");
            assertThat(ids).containsExactly(교대역id, 남부터미널역id, 양재역id);
            assertThat(distance).isEqualTo(5);
        });
    }
}
