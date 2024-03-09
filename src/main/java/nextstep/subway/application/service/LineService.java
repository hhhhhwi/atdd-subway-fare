package nextstep.subway.application.service;

import nextstep.subway.application.dto.LineRequest;
import nextstep.subway.application.dto.LineResponse;
import nextstep.subway.application.dto.SectionRequest;
import nextstep.subway.application.dto.SectionResponse;
import nextstep.subway.domain.entity.Line;
import nextstep.subway.domain.entity.Section;
import nextstep.subway.domain.repository.LineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {
	private final LineRepository lineRepository;
	private final StationService stationService;

	public LineService(LineRepository lineRepository, StationService stationService) {
		this.lineRepository = lineRepository;
		this.stationService = stationService;
	}

	@Transactional
	public LineResponse saveLine(LineRequest lineRequest) {
		Line line = new Line(lineRequest.getName(), lineRequest.getColor(), lineRequest.getStartStationId(), lineRequest.getEndStationId(), lineRequest.getDistance(), lineRequest.getDuration());

		return createLineResponse(lineRepository.save(line));
	}

	public List<LineResponse> findAllLines() {
		return lineRepository.findAll().stream()
				.map(this::createLineResponse).collect(Collectors.toList());
	}

	public LineResponse findLineById(Long id) {
		return lineRepository.findById(id)
				.map(this::createLineResponse)
				.orElseThrow(EntityNotFoundException::new);
	}

	@Transactional
	public void updateLine(Long id, LineRequest lineRequest) {
		Line line = findLindById(id);

		line.setUpdateInfo(lineRequest.getName(), lineRequest.getColor());

		lineRepository.save(line);
	}

	@Transactional
	public void deleteLine(Long id) {
		lineRepository.deleteById(id);
	}

	@Transactional
	public void addSection(Long id, SectionRequest sectionRequest) {
		Line line = findLindById(id);

		Section section = new Section(line, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance());
		line.addSection(section);
	}

	@Transactional
	public void deleteSection(Long id, Long stationId) {
		Line line = findLindById(id);

		line.deleteSection(stationId);
	}

	public List<SectionResponse> findSectionsByLine(Long id) {
		return findLindById(id).getSections()
				.convertToSectionResponse();
	}

	private Line findLindById(Long id) {
		return lineRepository.findById(id)
				.orElseThrow(EntityNotFoundException::new);
	}

	private LineResponse createLineResponse(Line line) {
		return new LineResponse(line,
				List.of(stationService.findStationById(line.getStartStationId()), stationService.findStationById(line.getEndStationId())));
	}
}
