package com.github.Snuslyk.slib.generators;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ClassGenerators {

    public static void generateHibernateObject(Class<?> mainClass, String name, String... f) {
        String packageName = mainClass.getPackage().getName();

        String code = "package {PACKAGE};\n" +
                "\n" +
                "import com.github.Snuslyk.slib.RowData;\n" +
                "\n" +
                "import javax.persistence.*;\n" +
                "\n" +
                "@Entity\n" +
                "@Table(name = \"{TABLE}\")\n" +
                "public class {NAME} implements RowData {\n" +
                "\n" +
                "    @Id\n" +
                "    @GeneratedValue(strategy = GenerationType.IDENTITY)\n" +
                "    private int id;\n" +
                "\n" +
                "    {FIELDS}" +
                "\n" +
                "    @Override\n" +
                "    public int getID() {\n" +
                "        return id;\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public int getColorData() {\n" +
                "        return -1;\n" +
                "    }\n" +
                "}\n";

        code = code.replace("{PACKAGE}", packageName);
        code = code.replace("{NAME}", name);

        StringBuilder fields = new StringBuilder();
        for (String field : f) {
            fields.append("public ").append(field).append(";\n");
        }
        code = code.replace("{FIELDS}", fields.toString());
        String tableName = name.substring(0, 1).toLowerCase() + name.substring(1);
        code = code.replace("{TABLE}", tableName);

        try {
            Files.writeString(new File(name+".java").toPath(), code);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
