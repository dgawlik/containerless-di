package io.dgawlik;


import io.dgawlik.annotation.Inject;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.lang.reflect.Constructor;
import java.util.Set;
import java.util.stream.Stream;

public class Scanner {
    private final String[] basePackages;

    public Scanner(String... basePackages) {
        if (basePackages.length < 1) {
            throw new IllegalArgumentException("At least one package must be specified.");
        }
        this.basePackages = basePackages;
    }

    public Set<Constructor> search() {

        var filters = new FilterBuilder();
        Stream.of(basePackages).forEach(filters::includePackage);

        var config = new ConfigurationBuilder()
                .forPackages(basePackages)
                .filterInputsBy(filters)
                .setScanners(Scanners.ConstructorsSignature,
                        Scanners.ConstructorsAnnotated);
        var refl = new Reflections(config);

        var set = refl.getConstructorsWithSignature();
        set.addAll(refl.getConstructorsAnnotatedWith(Inject.class));

        return set;
    }
}
