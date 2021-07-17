package bbangduck.bd.bbangduck.domain.follow.entity;

import lombok.RequiredArgsConstructor;

/**
 * 팔로우가 단방향 관계인지, 양방향 관계인지 구분하기 위한 상태값
 *
 * @author Gumin Jeong
 * @since 2021-07-16
 */
@RequiredArgsConstructor
public enum FollowStatus {
    ONE_WAY_FOLLOW("A -> B 단방향 팔로우"),
    TWO_WAY_FOLLOW("A <-> B 양방향 팔로우");

    private final String description;
}
