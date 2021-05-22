package org.folio.innreach.repository;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.folio.innreach.fixture.InnReachLocationFixture.createInnReachLocation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.jdbc.Sql;

import org.folio.innreach.domain.entity.InnReachLocation;
import org.folio.innreach.fixture.TestUtil;

@Sql(scripts = "classpath:db/pre-populate-inn-reach-location-code.sql")
class InnReachLocationRepositoryTest extends BaseRepositoryTest {

  private static final String PRE_POPULATED_LOCATION1_ID = "a1c1472f-67ec-4938-b5a8-f119e51ab79b";
  private static final String PRE_POPULATED_LOCATION2_ID = "26f7c8c5-f090-4742-b7c7-e08ed1cc4e67";
  private static final String PRE_POPULATED_LOCATION3_ID = "34c6a230-d264-44c5-90b3-6159ed2ebdc1";
  private static final String PRE_POPULATED_USER = "admin";


  @Autowired
  private InnReachLocationRepository locationRepository;


  @Test
  void shouldFindAllExistingLocations() {
    var locations = locationRepository.findAll();

    assertEquals(3, locations.size());

    List<String> ids = locations.stream()
        .map(innReachLocation -> innReachLocation.getId().toString())
        .collect(toList());

    assertEquals(ids, List.of(PRE_POPULATED_LOCATION1_ID, PRE_POPULATED_LOCATION2_ID, PRE_POPULATED_LOCATION3_ID));
  }

  @Test
  void shouldGetLocationWithMetadata() {
    var location = locationRepository.getOne(UUID.fromString(PRE_POPULATED_LOCATION1_ID));

    assertEquals(PRE_POPULATED_USER, location.getCreatedBy());
    assertNotNull(location.getCreatedDate());
    assertEquals(PRE_POPULATED_USER, location.getLastModifiedBy());
    assertNotNull(location.getLastModifiedDate());
  }

  @Test
  void shouldSaveNewLocation() {
    var newLocation = createInnReachLocation();

    var saved = locationRepository.save(newLocation);

    Optional<InnReachLocation> found = locationRepository.findById(saved.getId());
    assertTrue(found.isPresent());
    assertEquals(saved, found.get());
  }

  @Test
  void shouldUpdateLocationCodeAndDescription() {
    var location = locationRepository.getOne(UUID.fromString(PRE_POPULATED_LOCATION1_ID));

    String newCode = TestUtil.randomFiveCharacterCode();
    String newDescription = RandomStringUtils.randomAlphabetic(255);
    location.setCode(newCode);
    location.setDescription(newDescription);

    locationRepository.save(location);

    var saved = locationRepository.getOne(location.getId());

    assertEquals(newCode, saved.getCode());
    assertEquals(newDescription, saved.getDescription());
  }

  @Test
  void shouldDeleteExistingLocation() {
    UUID id = UUID.fromString(PRE_POPULATED_LOCATION1_ID);

    locationRepository.deleteById(id);

    Optional<InnReachLocation> deleted = locationRepository.findById(id);
    assertTrue(deleted.isEmpty());
  }

  @Test
  void throwExceptionWhenSavingWithoutRequiredData() {
    var location = createInnReachLocation();
    location.setCode(null);

    assertThrows(DataIntegrityViolationException.class, () -> locationRepository.saveAndFlush(location));
  }

  @Test
  void throwExceptionWhenCodeIsNotUnique() {
    var location = locationRepository.getOne(UUID.fromString(PRE_POPULATED_LOCATION1_ID));

    var newLocation = createInnReachLocation();
    newLocation.setCode(location.getCode());

    var ex = assertThrows(DataIntegrityViolationException.class, () -> locationRepository.saveAndFlush(newLocation));
    assertThat(ex.getMessage(), containsString("ConstraintViolationException"));
  }

}
