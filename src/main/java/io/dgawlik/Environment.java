package io.dgawlik;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.*;

public class Environment {
    private List<Map<String, String>> sources;

    public Environment() {
        sources = new ArrayList<>();
    }

    public Environment withClasspathFile(String path) {
        InputStream in = Environment.class.getResourceAsStream(path);
        if (in == null) {
            throw new RuntimeException("File not found in classpath, name=" + path);
        }

        var sc = new Scanner(in);
        Map<String, String> map = parse(sc);

        sc.close();
        sources.add(map);

        return this;
    }

    public Environment withFile(String path) {
        try {
            FileInputStream fin = new FileInputStream(path);

            var sc = new Scanner(fin);
            Map<String, String> map = parse(sc);

            sc.close();
            sources.add(map);

            return this;
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Environment didn't find file on path " + path, e);
        }
    }

    private Map<String, String> parse(Scanner sc) {
        Map<String, String> map = new HashMap<>();
        while (sc.hasNextLine()) {
            String[] kv = sc.nextLine().split("=");

            if (kv.length == 2) {
                map.put(kv[0].trim(), kv[1].trim());
            }
        }
        return map;
    }

    public Environment withMap(Map<String, String> map) {
        sources.add(map);
        return this;
    }

    public String getProperty(String key) {
        for (Map<String, String> map : sources) {
            if (map.containsKey(key)) {
                return map.get(key);
            }
        }

        return null;
    }
}
