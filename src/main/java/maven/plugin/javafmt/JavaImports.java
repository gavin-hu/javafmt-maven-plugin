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


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

@SuppressWarnings("serial")
public class JavaImports extends TreeSet<String> {

    public JavaImports() {
        //
        super(new Comparator<String>() {
            @Override
            public int compare(String lineImport1, String lineImport2) {
                //
                return lineImport1.compareTo(lineImport2);
            }
        });
    }

    public List<String> toLines() {
        String group = null;
        //
        List<String> lineImports = new ArrayList<String>();
        for (String lineImport : this) {
            String currentGroup = lineImport.substring("import ".length(), lineImport.indexOf("."));
            if (!currentGroup.equals(group)) {
                group = currentGroup;
                lineImports.add("\n");
            }
            lineImports.add(lineImport);
            lineImports.add("\n");
        }
        //
        return lineImports;
    }

    @Override
    public String toString() {
        //
        StringBuilder stringBuilder = new StringBuilder();
        for (String lineImport : toLines()) {
            stringBuilder.append(lineImport);
        }
        //
        return stringBuilder.toString();
    }

}
