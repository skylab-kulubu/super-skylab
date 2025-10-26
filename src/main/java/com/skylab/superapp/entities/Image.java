package com.skylab.superapp.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@Setter
@NoArgsConstructor
@DiscriminatorValue("IMAGE")
public class Image extends Media{

}
