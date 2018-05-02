package fr.kazejiyu.discord.rpc.integration.languages;

import java.util.Arrays;
import java.util.Collection;

public enum Language {
	
	UNKNOWN("", ""),
	
	ADA("ada", "Ada", "ada"),
	BOO("boo", "Boo", "boo"),
	CLOJURE("clojure", "Clojure", "clj"),
	COBOL("cobol", "Cobol", "cob", "cbl", "cpy"),
	CPP("cpp", "C++", "cpp", "hpp"),
	CRYSTAL("crystal", "Crystal", "cr"),
	CSS("css3", "CSS", "css"),
	GIT("git", "Git", "git", "gitignore"),
	HTML("html5", "HTML", "html"),
	JAVA("java", "Java", "java"),
	JAVASCRIPT("js", "JavaScript", "js"),
	MARKDOWN("markdown", "Markdown", "markdown", "mdown", "md"),
	SCALA("scala", "Scala", "scala", "sbt");
	
	private final String key;
	
	private final String name;
	
	private final Collection<String> extensions;
	
	private Language(String key, String name, String... extensions) {
		this.key = key;
		this.name = name;
		this.extensions = Arrays.asList(extensions);
	}
	
	public String getKey() {
		return this.key;
	}
	
	public String getName() {
		return this.name;
	}
	
	public static Language fromFileExtension(String fileName) {
		String extension = extensionOf(fileName);
		
		if (extension.isEmpty())
			return UNKNOWN;
		
		for (Language language : values())
			if (language.extensions.contains(extension))
				return language;
		
		return UNKNOWN;
	}

	private static String extensionOf(String fileName) {
		int dotIndex = fileName.lastIndexOf('.');
		
		if (dotIndex < 0 || fileName.length() <= dotIndex + 1)
			return "";
		
		String extension = fileName.substring(dotIndex + 1);
		extension = extension.toLowerCase();
		return extension;
	}
}
