package de.uni_leipzig.search_engine.backend.controller.search.dto;

import java.util.List;

import org.springframework.hateoas.Link;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * A DataTransferObject for a full search result page.
 * 
 * @author Maik Fröbe
 *
 */
@Data
@Accessors(chain=true)
public class SearchResultPage
{
	private String originalQuery;

	private int page;
	
	private List<SearchResult> resultsOnPage;
	
	private long totalHits;
	
	private Link nextPage;
	
	private Link previousPage;
	
	private Link firstPageLink;
	
	private Link lastPageLink;
	
	private List<Link> namedPageLinksBefore;
	
	private List<Link> namedPageLinksAfter;
}
