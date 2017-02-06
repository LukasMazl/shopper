package com.janprach.shopper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.inject.Inject;

import org.junit.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
public class ShopperAppITCase extends ShopperITBase {
	@Inject
	private ApplicationContext ctx;

	@Inject
	private MockMvc mvc;

	@Test
	public void testGetEstates() throws Exception {
		this.mvc.perform(get("/api/v1/estates")).andExpect(status().isOk())
				.andExpect(content().string(containsString("Awesome House")));
	}

	@Test
	public void testContextLoads() throws Exception {
		assertThat(this.ctx).isNotNull();
		assertThat(this.ctx.containsBean("estateResource")).isTrue();
	}
}
