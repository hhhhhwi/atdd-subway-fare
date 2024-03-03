Feature: 지하철 경로 검색 기능
  Background:
    #    지하철 노선도
    #               3km / 8min     1km / 3min
    #            강남 --------- 역삼 --------- 선릉
    #             |                          |
    #             |                          |   3km / 1min
    # 1km / 1min  |                         한티
    #             |                          |
    #             |                          |   6km / 1min
    #            양재 --------- 매봉 --------- 도곡
    #               1km / 8min    5km / 1min
    #
    #            남포 --------- 서면
    #               51km / 5min

    #   지하철 이용에 대한 운임은 아래 정책을 따른다.
    #    1. 지하철 운임은 탑승역과 하차역 사이의 최단거리에 비례하여 책정한다.
    #    2. 이동 거리가 10km 이하일시 기본요금으로 이용할 수 있다. (ex: 0 ~ 10km - 기본요금)
    #    3. 이동 거리가 10km 초과, 50km 이하일시 매 5km 마다 100원의 추가요금을 부가한다. (ex: 11km - 기본요금 + 추가요금 100원 / 15km - 기본요금 + 추가요금 200원)
    #    4. 이동 거리가 50km를 초과할시 매 8km 마다 100원의 추가요금을 부가한다. (ex: 51km - 이전까지의 요금 + 추가요금 100원)

    Given 지하철 역을 생성하고
      | name     |
      | 강남역     |
      | 역삼역     |
      | 선릉역     |
      | 한티역     |
      | 양재역     |
      | 매봉역     |
      | 도곡역     |
      | 남포역     |
      | 서면역     |
    And 지하철 노선을 생성하고
      | name   |   color  | upStationName | downStationName | distance |  duration  |
      | 2호선   |    초록   |      강남역     |       역삼역      |     3    |      8     |
      | 3호선   |    주황   |      양재역     |       매봉역      |     1    |      8     |
      | 신분당선  |   빨강    |     강남역      |      양재역       |     1    |      1     |
      | 분당선   |    노랑   |     선릉역      |       한티역      |     3    |      1     |
      | 부산선   |    검정   |     남포역      |       서면역      |    51    |      5     |
    And 지하철 노선의 구간들을 생성한다
      | name   | upStationName | downStationName | distance |  duration  |
      | 2호선   |      역삼역     |      선릉역       |     1    |      3     |
      | 3호선   |      매봉역     |      도곡역       |     5    |      1     |
      | 분당선   |      한티역     |      도곡역       |     6    |      6     |

    Scenario: 두 역 사이의 최단 거리 경로를 조회한다.
      When "매봉역"과 "역삼역"의 최단 거리 경로를 조회하면
      Then 두 역을 잇는 경로 중 거리가 가장 짧은 경로를 반환한다.
        |  매봉역   |
        |  양재역   |
        |  강남역   |
        |  역삼역   |
      And 거리가 가장 짧은 경로의 이동거리는 5km 소요시간은 17분이다.
      And 운임은 1250원이다.

    Scenario: 두 역 사이의 최단 시간 경로를 조회한다.
      When "매봉역"과 "역삼역"의 최단 시간 경로를 조회하면
      Then 두 역을 잇는 경로 중 소요시간이 가장 짧은 경로를 반환한다.
        |  매봉역   |
        |  도곡역   |
        |  한티역   |
        |  선릉역   |
        |  역삼역   |
      And 시간이 제일 적게 소요되는 경로의 이동거리는 15km 소요시간은 11분이다.
      And 운임은 1350원이다.

    Scenario: 거리가 50km를 초과하는 구간은 운임 계산 정책 4번을 적용한다.
      When 거리가 50km를 초과하는 "남포역"과 "서면역"의 경로를 조회하면
      And 운임은 2150원이다.

    Scenario: 이어지지 않는 두 역의 경로를 조회한다.
      When "강남역"과 "서면역"의 경로를 조회하면
      Then 에러가 발생한다.
