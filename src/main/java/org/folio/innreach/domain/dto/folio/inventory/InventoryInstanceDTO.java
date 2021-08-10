package org.folio.innreach.domain.dto.folio.inventory;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class InventoryInstanceDTO {

  private UUID id;
  private String title;
  private List<IdentifierDTO> identifiers;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class IdentifierDTO {

    @JsonProperty("identifierTypeId")
    private UUID id;
    private String value;
  }
}
