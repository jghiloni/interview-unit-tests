package com.ecsteam;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

@RunWith(SpringRunner.class)
@SpringBootTest
public class InterviewUnitTestsApplicationTests {

	static final String URL_PATTERN = "{baseUrl}/word/{word}";

	@Autowired
	Environment env;

	String baseUrl;
	TestRestTemplate restTemplate;

	@Before
	public void beforeClass() {
		if (!StringUtils.hasText(baseUrl)) {
			baseUrl = env.resolveRequiredPlaceholders("${test.base.url}");
		}

		if (restTemplate == null) {
			restTemplate = new TestRestTemplate();
		}
	}

	@Test
	public void testMultipleAnagrams() throws Exception {
		String word = "tea";

		long start = System.currentTimeMillis();
		ResponseEntity<AnagramResponse> responseEntity = restTemplate.getForEntity(URL_PATTERN,
			AnagramResponse.class, baseUrl, word);
		long end = System.currentTimeMillis();

		long timer = end - start;

		System.out.format("testMultipleAnagrams: Call /word/tea took %d ms\n", timer);

		Assert.assertEquals("HTTP Response should be 200", 200, responseEntity.getStatusCodeValue());
		Assert.assertEquals("Word should be set to tea", "tea", responseEntity.getBody().getWord());
		Assert.assertArrayEquals("Anagrams should be in the correct order and not include the original word",
			new String[]{"ate", "eat"},
			responseEntity.getBody().getAnagrams().toArray());

		word = "glare";
		start = System.currentTimeMillis();
		responseEntity = restTemplate.getForEntity(URL_PATTERN,	AnagramResponse.class, baseUrl, word);
		end = System.currentTimeMillis();
		timer = end - start;

		System.out.format("testMultipleAnagrams: Call /word/glare took %d ms\n", timer);

		Assert.assertEquals("HTTP Response should be 200", 200, responseEntity.getStatusCodeValue());
		Assert.assertEquals("Word should be set to glare", "glare", responseEntity.getBody().getWord());
		Assert.assertArrayEquals("Anagrams should be in the correct order and not include the original word",
			new String[]{"Alger", "lager", "large", "regal"},
			responseEntity.getBody().getAnagrams().toArray());

		Assert.assertTrue("Words should be in the original case",
			!responseEntity.getBody().getAnagrams().contains("alger"));
	}

	@Test
	public void testInvalidWord() throws Exception {
		String word = "asdfqerowpaslfj";

		long start = System.currentTimeMillis();
		ResponseEntity<AnagramErrorResponse> responseEntity = restTemplate.getForEntity(URL_PATTERN,
			AnagramErrorResponse.class, baseUrl, word);
		long end = System.currentTimeMillis();

		long timer = end - start;

		System.out.format("testInvalidWord: Call /word/asdfqerowpaslfj took %d ms\n", timer);

		Assert.assertEquals("HTTP Response should be 404", 404, responseEntity.getStatusCodeValue());
		Assert.assertEquals("Message should match spec", "Couldn't find word asdfqerowpaslfj",
			responseEntity.getBody().getMessage());
	}

	@Test
	public void testMissingWord() throws Exception {
		String word = "";

		long start = System.currentTimeMillis();
		ResponseEntity<AnagramErrorResponse> responseEntity = restTemplate.getForEntity(URL_PATTERN,
			AnagramErrorResponse.class, baseUrl, word);
		long end = System.currentTimeMillis();

		long timer = end - start;

		System.out.format("testMissingWord: Call /word/ took %d ms\n", timer);

		Assert.assertEquals("HTTP Response should be 404", 404, responseEntity.getStatusCodeValue());
	}
}


