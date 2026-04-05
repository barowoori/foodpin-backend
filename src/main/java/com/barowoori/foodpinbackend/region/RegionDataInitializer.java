package com.barowoori.foodpinbackend.region;

import com.barowoori.foodpinbackend.region.command.domain.repository.RegionDoRepository;
import com.barowoori.foodpinbackend.region.command.domain.repository.RegionGuRepository;
import com.barowoori.foodpinbackend.region.command.domain.repository.RegionGunRepository;
import com.barowoori.foodpinbackend.region.command.domain.repository.RegionSiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class RegionDataInitializer implements ApplicationRunner {

    private final RegionDoRepository regionDoRepository;
    private final RegionSiRepository regionSiRepository;
    private final RegionGuRepository regionGuRepository;
    private final RegionGunRepository regionGunRepository;
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        initializeRegionDo();
        initializeRegionSi();
        initializeRegionGu();
        initializeRegionGun();
    }

    private void initializeRegionDo() throws Exception {
        try (BufferedReader reader = openReader("seeds/region_do.csv")) {
            skipHeader(reader);
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = parseCsvLine(line, 2);
                String id = values[0];
                if (regionDoRepository.existsById(id)) {
                    continue;
                }

                jdbcTemplate.update(
                        "INSERT INTO region_do (id, name) VALUES (?, ?)",
                        id,
                        values[1]
                );
            }
        }
    }

    private void initializeRegionSi() throws Exception {
        try (BufferedReader reader = openReader("seeds/region_si.csv")) {
            skipHeader(reader);
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = parseCsvLine(line, 3);
                String id = values[0];
                if (regionSiRepository.existsById(id)) {
                    continue;
                }

                jdbcTemplate.update(
                        "INSERT INTO region_si (id, name, region_do_id) VALUES (?, ?, ?)",
                        id,
                        values[1],
                        nullable(values[2])
                );
            }
        }
    }

    private void initializeRegionGu() throws Exception {
        try (BufferedReader reader = openReader("seeds/region_gu.csv")) {
            skipHeader(reader);
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = parseCsvLine(line, 4);
                String id = values[0];
                if (regionGuRepository.existsById(id)) {
                    continue;
                }

                jdbcTemplate.update(
                        "INSERT INTO region_gu (id, name, region_do_id, region_si_id) VALUES (?, ?, ?, ?)",
                        id,
                        values[1],
                        nullable(values[2]),
                        nullable(values[3])
                );
            }
        }
    }

    private void initializeRegionGun() throws Exception {
        try (BufferedReader reader = openReader("seeds/region_gun.csv")) {
            skipHeader(reader);
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = parseCsvLine(line, 5);
                String id = values[0];
                if (regionGunRepository.existsById(id)) {
                    continue;
                }

                jdbcTemplate.update(
                        "INSERT INTO region_gun (id, name, region_do_id, region_gu_id, region_si_id) VALUES (?, ?, ?, ?, ?)",
                        id,
                        values[1],
                        nullable(values[2]),
                        nullable(values[3]),
                        nullable(values[4])
                );
            }
        }
    }

    private BufferedReader openReader(String path) throws Exception {
        return new BufferedReader(
                new InputStreamReader(new ClassPathResource(path).getInputStream(), StandardCharsets.UTF_8)
        );
    }

    private void skipHeader(BufferedReader reader) throws Exception {
        reader.readLine();
    }

    private String[] parseCsvLine(String line, int expectedLength) {
        String[] values = line.replace("\"", "").split(",", -1);
        if (values.length != expectedLength) {
            throw new IllegalArgumentException("Unexpected csv column length: " + line);
        }
        return values;
    }

    private String nullable(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}
