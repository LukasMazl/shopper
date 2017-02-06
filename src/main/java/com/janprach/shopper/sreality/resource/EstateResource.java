package com.janprach.shopper.sreality.resource;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.janprach.shopper.sreality.service.EstateService;

import lombok.AllArgsConstructor;

@AllArgsConstructor(onConstructor = @__({ @javax.inject.Inject }))
@RestController
public class EstateResource {
	private final EstateService estateService;

	@PutMapping("/api/v1/star")
	public void vote(@RequestParam final long srealityId, @RequestParam final int stars) {
		this.estateService.setStarsFor(srealityId, stars);
	}

	@PutMapping("/api/v1/visible")
	public void setVisible(@RequestParam final long srealityId,
			@RequestParam final boolean visible) {
		this.estateService.setVisibleFor(srealityId, visible);
	}

	@PutMapping("/api/v1/note")
	public void setNote(@RequestParam final long srealityId, @RequestParam final String note) {
		this.estateService.setNoteFor(srealityId, note);
	}
}
