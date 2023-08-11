package org.osetrm.api.uti.generator;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record UtiRequest(
        @NotNull(message = "RegulatoryRegime must not be null") RegulatoryRegime regulatoryRegime,
        @NotEmpty @Length(min = 20, max = 20, message = "LEI must be 20 characters in length") String lei) {
}
