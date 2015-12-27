package com.iolab.sightlocator;

import java.util.Locale;

public class Language {

public static String[] getDisplayLanguagesFromAbbrArray(String[] languagesAbbr){
	String[] techArray = new String[languagesAbbr.length];
	for (int i = 0; i < languagesAbbr.length; i++) {
		techArray[i] = getFullLanguageName(languagesAbbr[i]);
		}
	return techArray;
	}
public static String getFullLanguageName(String abbr){
	Locale locale = new Locale(abbr);
	return locale.getDisplayLanguage();
	}
}
