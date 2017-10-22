package de.uni_leipzig.search_engine_uni.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import de.uni_leipzig.search_engine_uni.lucene.SearcherComponent;

@Controller
public class RedirectController
{
	@Autowired
	private SearcherComponent searcherComponent;
	
	@RequestMapping(method=RequestMethod.GET, path="/url")
	public RedirectView redirect(@RequestParam Integer documentId)
	{
		return new RedirectView(searcherComponent.doc(documentId).get("link"));
	}
}
