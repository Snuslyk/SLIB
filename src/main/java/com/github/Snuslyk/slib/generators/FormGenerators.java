package com.github.Snuslyk.slib.generators;

public class FormGenerators {

    public static void generateForm(Class<?> clazz) {

        String c = clazz.getSimpleName();

        String lowercaseName = c.substring(0,1).toLowerCase() + c.substring(1);

        String code = "public static Form {LOWER}() {\n" +
                "        return new Form.Builder()\n" +
                "                .add(new TableFormType()\n" +
                "                        .name(\"\")\n" +
                "                        .type({CLASS}.class)\n" +
                "                        .column(\"\", \"\")\n" +
                "\n" +
                "                        .cellActionButton(TableFormType.TableActionButtons.DELETE)\n" +
                "                        .cellActionButton(TableFormType.TableActionButtons.EDIT)\n" +
                "                )\n" +
                "\n" +
                "                .add(new CreateFormType<>()\n" +
                "                        .name(\"\")\n" +
                "                        .type({CLASS}.class)\n" +
                "                        .instanceSupplier({CLASS}::new)\n" +
                "\n" +
                "                        .createSupplier((object, fields) -> {\n" +
                "                            {CLASS} {LOWER} = (CLASS) object;\n" +
                "                            for (AbstractField field : fields) {\n" +
                "                                switch (field.getKey()) {\n" +
                "                                    case \"\" -> {\n" +
                "                                        \n" +
                "                                    }\n" +
                "                                }\n" +
                "                            }\n" +
                "                            return {LOWER};\n" +
                "                        })\n" +
                "\n" +
                "                )\n" +
                "\n" +
                "                .build();\n" +
                "    }";

        System.out.println(code.replace("{LOWER}", lowercaseName).replace("{CLASS}", c));;


    }
}
