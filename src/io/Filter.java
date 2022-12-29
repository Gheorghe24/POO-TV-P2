package io;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class Filter {
    private Sort sort;
    private Contains contains;
}
