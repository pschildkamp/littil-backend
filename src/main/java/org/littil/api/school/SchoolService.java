package org.littil.api.school;

import lombok.RequiredArgsConstructor;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional
@ApplicationScoped
@RequiredArgsConstructor
public class SchoolService {

    private final SchoolRepository repository;
    private final SchoolMapper mapper;

    public SchoolDto getSchoolByName(final String name) {
        return mapper.schoolToSchoolDto(repository.findByName(name));
    }

    public SchoolDto getSchoolById(final Long id) {
        return mapper.schoolToSchoolDto(repository.findById(id));
    }

    public Set<SchoolDto> saveSchool(final SchoolDto schoolDto) {
        // todo: for example validations

        repository.persist(mapper.schoolDtoToSchool(schoolDto));
        return getAll();
    }

    public Set<SchoolDto> getAll() {
        return repository.streamAll().map(mapper::schoolToSchoolDto).collect(Collectors.toSet());
    }

    public Set<SchoolDto> deleteSchool(SchoolDto schoolDto) {
        // todo: for example check if exists

        repository.delete(mapper.schoolDtoToSchool(schoolDto));
        return getAll();
    }
}
