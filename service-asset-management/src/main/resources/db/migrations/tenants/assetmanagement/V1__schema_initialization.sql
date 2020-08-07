-- Asset Management
create table assets
(
    id uuid not null constraint assets_pkey primary key,
    asset_type_id      uuid,
    background_color varchar,
    border_color     varchar,
    created_by       varchar,
    created_date     timestamp,
    description     varchar(1000),
    foreground_color varchar,
    icon            varchar,
    image_url        varchar,
    name            varchar,
    parent_id        uuid,
    token           varchar,
    updated_by       varchar,
    updated_date     timestamp
);

create table assets_metadata
(
    asset_id uuid not null constraint fkhmfvnp287p256qi7bkg5dew6u references assets,
    prop_value varchar,
    prop_key   varchar not null,
    constraint assets_metadata_pkey primary key (asset_id, prop_key)
);

create table asset_types
(
    id uuid not null constraint asset_types_pkey primary key,
    background_color varchar,
    border_color     varchar,
    created_by       varchar,
    created_date     timestamp,
    description      varchar,
    foreground_color varchar,
    icon             varchar,
    image_url        varchar,
    name             varchar,
    token            varchar,
    updated_by       varchar,
    updated_date     timestamp,
    asset_category   varchar
);

create table asset_types_metadata
(
    asset_type_id uuid not null constraint fk13869vnjm3fh73tfou4sr7onb references asset_types,
    prop_value   varchar,
    prop_key     varchar not null,
    constraint asset_types_metadata_pkey primary key (asset_type_id, prop_key)
);
