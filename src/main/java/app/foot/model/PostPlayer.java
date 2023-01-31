package app.foot.model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@ToString
@EqualsAndHashCode
public class PostPlayer {
    private String name;
    private Boolean isGuardian;
    private Integer team;
}
