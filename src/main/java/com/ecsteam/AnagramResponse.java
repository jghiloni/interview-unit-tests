package com.ecsteam;

import lombok.Data;

import java.util.List;

/**
 * Created by josh on 11/30/16.
 */
@Data
public class AnagramResponse {
	private String word;
	private List<String> anagrams;
}
