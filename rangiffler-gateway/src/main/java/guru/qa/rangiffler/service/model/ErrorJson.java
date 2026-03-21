package guru.qa.rangiffler.service.model;

import jakarta.annotation.Nonnull;

public record ErrorJson(@Nonnull String type,
                        @Nonnull String title,
                        int status,
                        @Nonnull String detail,
                        @Nonnull String instance) {

}
