package nextstep.subway.domain;

import nextstep.subway.domain.entity.Section;

import java.util.List;

public class SubwayMapByDuration extends SubwayMap {
	public SubwayMapByDuration(List<Section> sections) {
		super(sections);
	}

	@Override
	protected void addSection(Section section) {
		weightedMultigraph.addVertex(section.getDownStationId());
		weightedMultigraph.addVertex(section.getUpStationId());

		SectionWeightedEdge edge = new SectionWeightedEdge(section);
		weightedMultigraph.addEdge(section.getDownStationId(), section.getUpStationId(), edge);

		weightedMultigraph.setEdgeWeight(edge, section.getDuration());
	}
}
