package io;

import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public final class Credentials {
    private String name;
    private String password;
    private String accountType;
    private String country;
    private String balance;

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Credentials that = (Credentials) o;
        return Objects.equals(name, that.name) && Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, password);
    }

    public Credentials(final Credentials credentials) {
        if (credentials != null) {
            this.name = credentials.name;
            this.password = credentials.password;
            this.accountType = credentials.accountType;
            this.country = credentials.country;
            this.balance = credentials.balance;
        }
    }
}
