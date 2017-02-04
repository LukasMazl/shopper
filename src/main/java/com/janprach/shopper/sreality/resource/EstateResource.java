package com.janprach.shopper.sreality.resource;

import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.janprach.shopper.sreality.service.EstateService;

@AllArgsConstructor(onConstructor = @__({ @javax.inject.Inject }))
@RestController
public class EstateResource {
	private final EstateService estateService;

	@RequestMapping("/api/v1/vote")
	public void vote(@RequestParam final long srealityId,
			@RequestParam final int stars) {
		this.estateService.setStarsFor(srealityId, stars);
	}

	@RequestMapping("/api/v1/setVisible")
	public void setVisible(@RequestParam final long srealityId,
			@RequestParam final boolean visible) {
		this.estateService.setVisibleFor(srealityId, visible);
	}

	@RequestMapping("/api/v1/setNote")
	public void setNote(@RequestParam final long srealityId,
			@RequestParam final String note) {
		this.estateService.setNoteFor(srealityId, note);
	}
}
