create table ratings(
    id bigint auto_increment,
    movie_id bigint,
    rating int,
    constraint pk_ratings primary key (id)
);
