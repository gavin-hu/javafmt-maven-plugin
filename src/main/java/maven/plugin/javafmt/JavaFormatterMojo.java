package maven.plugin.javafmt;

/*
 * #%L
 * Java Formatter Maven Plugin
 * %%
 * Copyright (C) 2013 FocusSNS
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 2.1 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */


import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.text.edits.TextEdit;

@Mojo(name = "format", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class JavaFormatterMojo extends AbstractMojo {
    private static final String LINE_ENDING_CRLF_CHARS = "\r\n";
    @Parameter(property = "basedir")
    private String basedir;
    @Parameter(property = "fileEncoding", defaultValue = "${project.build.sourceEncoding}")
    private String fileEncoding;
    @Parameter(property = "codeStyleFile")
    private String codeStyleFile;
    @Parameter(property = "compilerSource", required = true, defaultValue = "${maven.compiler.source}")
    private String compilerSource;
    @Parameter(property = "compilerCompliance", required = true, defaultValue = "${maven.compiler.source}")
    private String compilerCompliance;
    @Parameter(property = "compilerTargetPlatform", required = true, defaultValue = "${maven.compiler.target}")
    private String compilerTargetPlatform;

    public void setBasedir(String basedir) {
        this.basedir = basedir;
    }

    public void setFileEncoding(String fileEncoding) {
        this.fileEncoding = fileEncoding;
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            CodeFormatter codeFormatter = getCodeFormatter();
            //
            File srcDir = new File(basedir, "src");
            Iterator<File> iter = FileUtils.listFiles(srcDir, new String[] { "java" }, true).iterator();
            while (iter.hasNext()) {
                File javaFile = iter.next();
                getLog().info("Format Java File : " + javaFile.getAbsolutePath());
                //
                formatJavaFile(codeFormatter, javaFile);
            }
        } catch (Exception e) {
            throw new MojoExecutionException("Java Code Format Exception!", e);
        }
    }

    protected void formatJavaFile(CodeFormatter codeFormatter, File javaFile) throws IOException, BadLocationException, IOException {
        //
        String javaCode = FileUtils.readFileToString(javaFile, fileEncoding);
        IDocument javaDoc = new Document(javaCode);
        //
        javaDoc = organizeImports(javaDoc);
        //
        javaDoc = formatCode(codeFormatter, javaDoc);
        //
        FileUtils.write(javaFile, javaDoc.get(), fileEncoding);
    }

    protected IDocument organizeImports(IDocument javaDoc) throws BadLocationException {
        //
        int firstImportCount = 0;
        int lastImportCount = 0;
        int linesNumber = javaDoc.getNumberOfLines();
        JavaImports javaImports = new JavaImports();
        //
        for (int lineNumber = 0; lineNumber < linesNumber; lineNumber++) {
            IRegion region = javaDoc.getLineInformation(lineNumber);
            String line = javaDoc.get(region.getOffset(), region.getLength());
            //
            if (line.startsWith("import")) {
                //
                javaImports.add(line);
                //
                if (firstImportCount == 0) {
                    firstImportCount = lineNumber;
                }
                //
                lastImportCount = lineNumber;
            }
        }
        //
        StringBuilder javaCodeBuilder = new StringBuilder();
        for (int lineNumber = 0; lineNumber < linesNumber; lineNumber++) {
            //
            IRegion region = javaDoc.getLineInformation(lineNumber);
            String line = javaDoc.get(region.getOffset(), region.getLength());
            //
            if (lineNumber >= firstImportCount && lineNumber <= lastImportCount) {
                continue;
            }
            //
            if (lineNumber == lastImportCount + 1) {
                javaCodeBuilder.append(javaImports);
            }
            javaCodeBuilder.append(line).append(LINE_ENDING_CRLF_CHARS);
        }
        //
        javaDoc.set(javaCodeBuilder.toString());
        //
        return javaDoc;
    }

    protected IDocument formatCode(CodeFormatter codeFormatter, IDocument javaDoc) throws BadLocationException {
        //
        String javaCode = javaDoc.get();
        int kind = CodeFormatter.K_COMPILATION_UNIT + CodeFormatter.F_INCLUDE_COMMENTS;
        //
        TextEdit textEdit = codeFormatter.format(kind, javaCode, 0, javaCode.length(), 0, LINE_ENDING_CRLF_CHARS);
        //
        textEdit.apply(javaDoc);
        //
        return javaDoc;
    }

    protected CodeFormatter getCodeFormatter() throws Exception {
        //
        Map<String, String> defaultOptions = new HashMap<String, String>();
        defaultOptions.put(JavaCore.COMPILER_SOURCE, compilerSource);
        defaultOptions.put(JavaCore.COMPILER_COMPLIANCE, compilerCompliance);
        defaultOptions.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, compilerTargetPlatform);
        //
        URL url = getClass().getClassLoader().getResource("META-INF/java-code-style.xml");
        if (codeStyleFile != null) {
            url = URI.create("file:/" + codeStyleFile).toURL();
        }
        JavaCodeStyle javaCodeStyle = new JavaCodeStyle(defaultOptions, url.openStream());
        //
        return ToolFactory.createCodeFormatter(javaCodeStyle);
    }

}
