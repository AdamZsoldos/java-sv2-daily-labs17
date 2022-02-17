create table actor_to_movie(
    id bigint auto_increment,
    actor_id bigint,
    movie_id bigint,
    constraint pk_actor_to_movie primary key(id)
);