package io;

import java.util.ArrayList;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Contains {
    private ArrayList<String> actors;
    private ArrayList<String> genre;
    private ArrayList<String> country;
}
