/*******************************************************************************
 * Copyright (C) 2018-2019 Emmanuel CHEBBI
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package fr.kazejiyu.discord.rpc.integration.languages;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Programming languages handled by the plug-in.
 * 
 * @author Emmanuel CHEBBI
 */
// TODO [Refactor] Consider using an OO architecture to benefit from polymorphism
public enum Language {
    
    UNKNOWN("", ""),
    
    ADA("ada", "Ada", "ada"),
    BINARY("binary", "Binary", "bin"),
    BOO("boo", "Boo", "boo"),
    CLOJURE("clojure", "Clojure", "clj"),
    COBOL("cobol", "Cobol", "cob", "cbl", "cpy"),
    C("c", "C", "c", "h"),
    CPP("cpp", "C++", "cpp", "hpp"),
    CRYSTAL("crystal", "Crystal", "cr"),
    CSS("css3", "CSS", "css"),
    CSHARP("csharp", "C#", "cs"),
    DART("dart", "Dart", "dart"),
    DOCKER("docker", "Docker", emptyList(), asList("Dockerfile")),
    FORTRAN("fortran", "Fortran", "f", "for"),
    FORTRAN90("fortran", "Fortran 90", "f90"),
    FORTRAN95("fortran", "Fortran 95", "f95"),
    FORTRAN03("fortran", "Fortran 2003", "f03"),
    GIT("git", "Git", asList("git"), asList(".gitignore", ".gitattributes", ".gitmodules")),
    GO("go", "Go", "go"),
    GRADLE("gradle", "Gradle", "gradle"),
    GROOVY("groovy", "Groovy", "groovy", "gvy", "gy", "gsh"),
    HASKELL("haskell", "Haskell", "hs"),
    HTML("html5", "HTML", "html"),
    JAVA("java", "Java", "java"),
    JAVASCRIPT("js", "JavaScript", "js"),
    KOTLIN("kotlin", "Kotlin", "kt", "ktm", "kts"),
    LATEX("bibtex", "LaTeX", "tex"),
    LISP("lisp", "Lisp", "lisp", "lsp"),
    LUA("lua", "Lua", "lua"),
    MARKDOWN("markdown", "Markdown", "markdown", "mdown", "md"),
    OCAML("ocaml", "OCaml", "ml", "mli"),
    PASCAL("pascal", "Pascal", "pas"),
    PHP("php", "PHP", "php"),
    PROLOG("prolog", "Prolog", "pro", "pl"),
    PYTHON("python", "Python", "py"),
    R("r", "R", "r"),
    RUBY("ruby", "Ruby", "rb"),
    RUST("rust", "Rust", "rs", "rlib"),
    SCALA("scala", "Scala", "scala", "sbt"),
    SQL("sql", "SQL", "sql"),
    SWIFT("swift", "Swift", "swift"),
    TERMINAL("terminal", "OS Scripting Language", "sh", "bash", "ksh"),
    TEXT("text", "Text", "txt"),
    TYPESCRIPT("ts", "TypeScript", "ts");
    
    /** Identifies the icon to display in Discord. */
    private final String key;
    
    /** Human-readable name of the language. */
    private final String name;
    
    /** Used for languages using file extension (.cpp, .xml, .json, ...) */
    private final Collection<String> extensions;
    
    /** Used for languages using special files (Dockerfile, pom.xml, ...) */
    private final Collection<String> fileNames;
    
    /**
     * Defines a new language.
     * 
     * @param key
     *             The identifier of the language as registered in the Discord Application
     * @param name
     *             The string shown in Discord's interface
     * @param extensions
     *             The file extensions of the language
     */
    private Language(String key, String name, String... extensions) {
        this(key, name, asList(extensions), emptyList());
    }
    
    /**
     * Defines a new language.
     * 
     * @param key
     *             The identifier of the language as registered in the Discord Application
     * @param name
     *             The string shown in Discord's interface
     * @param extensions
     *             The file extensions of the language
     * @param fileNames
     *             Names of special files related to the language
     */
    private Language(String key, String name, List<String> extensions, List<String> fileNames) {
        this.key = key;
        this.name = name;
        this.extensions = extensions;
        this.fileNames = fileNames;
    }
    
    /** 
     * Returns the Discord key identifying language's icon.
     * @return the Discord key identifying language's icon
     */
    public String getKey() {
        return this.key;
    }
    
    /** 
     * Returns a human-readable name of the language.
     * @return the name of the language
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * Returns all the file extensions related to this language. 
     * @return the file extensions related to this language 
     */
    public Set<String> getFileExtensions() {
        return new LinkedHashSet<>(this.extensions);
    }
    
    /** 
     * Returns the name of all the files related to this language.
     * @return the name of the files related to this language 
     */
    public Set<String> getFileNames() {
        return new LinkedHashSet<>(this.fileNames);
    }
    
    /**
     * <p>Returns the language corresponding to the given file name.</p>
     * 
     * <p>If no language can be found, {@link #UNKNOWN} is returned.</p>
     *  
     * @param fileName
     *             The name of the file which language is looked for.
     * 
     * @return the language corresponding to the given file name
     */
    public static Language fromFileName(String fileName) {
        for (Language language : values()) {
            if (language.fileNames.contains(fileName)) {
                return language;
            }
        }
        String extension = extensionOf(fileName);
        
        if (extension.isEmpty()) {
            return UNKNOWN;
        }
        for (Language language : values()) {
            if (language.extensions.contains(extension)) {
                return language;
            }
        }
        return UNKNOWN;
    }

    /** Returns the extension, in lower case and without the final dot, of {@code fileName}. */
    private static String extensionOf(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        
        if (dotIndex <= 0 || fileName.length() <= dotIndex + 1) {
            return "";
        }
        String extension = fileName.substring(dotIndex + 1);
        return extension.toLowerCase();
    }
}
