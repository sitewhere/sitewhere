create table area
(
    id              uuid not null
        constraint area_pkey
            primary key,
    areatypeid      uuid,
    backgroundcolor varchar(255),
    bordercolor     varchar(255),
    createdby       varchar(255),
    createddate     timestamp,
    description     varchar(1000),
    foregroundcolor varchar(255),
    icon            varchar(255),
    imageurl        varchar(255),
    name            varchar(255),
    parentid        uuid,
    token           varchar(255),
    updatedby       varchar(255),
    updateddate     timestamp
);

alter table area
    owner to sitewhere;

create table area_metadata
(
    area_id   uuid         not null
        constraint fkhmfvnp287p956qi7bkg5dew6u
            references area,
    propvalue varchar(255),
    propkey   varchar(255) not null,
    constraint area_metadata_pkey
        primary key (area_id, propkey)
);

alter table area_metadata
    owner to sitewhere;

create table area_type
(
    id              uuid not null
        constraint area_type_pkey
            primary key,
    backgroundcolor varchar(255),
    bordercolor     varchar(255),
    createdby       varchar(255),
    createddate     timestamp,
    description     varchar(255),
    foregroundcolor varchar(255),
    icon            varchar(255),
    imageurl        varchar(255),
    name            varchar(255),
    token           varchar(255),
    updatedby       varchar(255),
    updateddate     timestamp
);

alter table area_type
    owner to sitewhere;

create table area_type_metadata
(
    areatype_id uuid         not null
        constraint fk63869vnjm3fh73tfou4sr7enb
            references area_type,
    propvalue   varchar(255),
    propkey     varchar(255) not null,
    constraint area_type_metadata_pkey
        primary key (areatype_id, propkey)
);

alter table area_type_metadata
    owner to sitewhere;

create table areatype_containedareatypeids
(
    areatype_id          uuid not null
        constraint fkivgj2wi2k2rbhlfao83urn8s0
            references area_type,
    containedareatypeids uuid
);

alter table areatype_containedareatypeids
    owner to sitewhere;

create table command_parameter
(
    id       uuid    not null
        constraint command_parameter_pkey
            primary key,
    name     varchar(255),
    required boolean not null,
    type     varchar(255)
);

alter table command_parameter
    owner to sitewhere;

create table customer
(
    id              uuid not null
        constraint customer_pkey
            primary key,
    backgroundcolor varchar(255),
    bordercolor     varchar(255),
    createdby       varchar(255),
    createddate     timestamp,
    customertypeid  uuid,
    description     varchar(255),
    foregroundcolor varchar(255),
    icon            varchar(255),
    imageurl        varchar(255),
    name            varchar(255),
    parentid        uuid,
    token           varchar(255),
    updatedby       varchar(255),
    updateddate     timestamp
);

alter table customer
    owner to sitewhere;

create table customer_metadata
(
    customer_id uuid         not null
        constraint fkegp0a66oeynm3mfdptp0fwdxc
            references customer,
    propvalue   varchar(255),
    propkey     varchar(255) not null,
    constraint customer_metadata_pkey
        primary key (customer_id, propkey)
);

alter table customer_metadata
    owner to sitewhere;

create table customer_type
(
    id              uuid not null
        constraint customer_type_pkey
            primary key,
    backgroundcolor varchar(255),
    bordercolor     varchar(255),
    createdby       varchar(255),
    createddate     timestamp,
    description     varchar(255),
    foregroundcolor varchar(255),
    icon            varchar(255),
    imageurl        varchar(255),
    name            varchar(255),
    token           varchar(255),
    updatedby       varchar(255),
    updateddate     timestamp
);

alter table customer_type
    owner to sitewhere;

create table customer_type_metadata
(
    customertype_id uuid         not null
        constraint fkh73yk0mon5ls1poxue1kwmis3
            references customer_type,
    propvalue       varchar(255),
    propkey         varchar(255) not null,
    constraint customer_type_metadata_pkey
        primary key (customertype_id, propkey)
);

alter table customer_type_metadata
    owner to sitewhere;

create table customertype_containedcustomertypeids
(
    customertype_id          uuid not null
        constraint fk54criwc27w7ln3plk646ana8l
            references customer_type,
    containedcustomertypeids uuid
);

alter table customertype_containedcustomertypeids
    owner to sitewhere;

create table device
(
    id                 uuid not null
        constraint device_pkey
            primary key,
    comments           varchar(255),
    createdby          varchar(255),
    createddate        timestamp,
    deviceassignmentid uuid,
    devicetypeid       uuid,
    devicetypetoken    varchar(255),
    parentdeviceid     uuid,
    status             varchar(255),
    token              varchar(255),
    updatedby          varchar(255),
    updateddate        timestamp
);

alter table device
    owner to sitewhere;

create table device_activedeviceassignmentids
(
    device_id                 uuid not null
        constraint fk3fi15qo5rgptuvrfwasn5dn1l
            references device,
    activedeviceassignmentids uuid
);

alter table device_activedeviceassignmentids
    owner to sitewhere;

create table device_alarm
(
    id                 uuid not null
        constraint device_alarm_pkey
            primary key,
    acknowledgeddate   timestamp,
    alarmmessage       varchar(255),
    areaid             uuid,
    assetid            uuid,
    customerid         uuid,
    deviceassignmentid uuid,
    deviceid           uuid,
    resolveddate       timestamp,
    state              varchar(255),
    triggereddate      timestamp,
    triggeringeventid  uuid
);

alter table device_alarm
    owner to sitewhere;

create table device_alarm_metadata
(
    devicealarm_id uuid         not null
        constraint fk7frl1jp0oylh4nkyj4x3g4tjb
            references device_alarm,
    propvalue      varchar(255),
    propkey        varchar(255) not null,
    constraint device_alarm_metadata_pkey
        primary key (devicealarm_id, propkey)
);

alter table device_alarm_metadata
    owner to sitewhere;

create table device_alot
(
    id   uuid not null
        constraint device_alot_pkey
            primary key,
    name varchar(255),
    path varchar(255)
);

alter table device_alot
    owner to sitewhere;

create table device_assignment
(
    id           uuid not null
        constraint device_assignment_pkey
            primary key,
    activedate   timestamp,
    areaid       uuid,
    assetid      uuid,
    createdby    varchar(255),
    createddate  timestamp,
    customerid   uuid,
    deviceid     uuid,
    devicetypeid uuid,
    releaseddate timestamp,
    status       varchar(255),
    token        varchar(255),
    updatedby    varchar(255),
    updateddate  timestamp
);

alter table device_assignment
    owner to sitewhere;

create table device_assignment_metadata
(
    deviceassignment_id uuid         not null
        constraint fkj09gyp8kafgn9m9e5oeqwdqms
            references device_assignment,
    propvalue           varchar(255),
    propkey             varchar(255) not null,
    constraint device_assignment_metadata_pkey
        primary key (deviceassignment_id, propkey)
);

alter table device_assignment_metadata
    owner to sitewhere;

create table device_command
(
    id              uuid not null
        constraint device_command_pkey
            primary key,
    createdby       varchar(255),
    createddate     timestamp,
    description     varchar(255),
    devicetypeid    uuid,
    devicetypetoken varchar(255),
    name            varchar(255),
    namespace       varchar(255),
    token           varchar(255),
    updatedby       varchar(255),
    updateddate     timestamp
);

alter table device_command
    owner to sitewhere;

create table device_command_command_parameter
(
    devicecommand_id uuid not null
        constraint fkorjga3uqnhegt5l9aepty8nl0
            references device_command,
    parameterlist_id uuid not null
        constraint uk_rdcnf3edn23qfpkew891jgt7c
            unique
        constraint fk7ss3l5iucmjsehmkrdxsdakjd
            references command_parameter
);

alter table device_command_command_parameter
    owner to sitewhere;

create table device_command_metadata
(
    devicecommand_id uuid         not null
        constraint fkhndjvpa5li7al6pwcne8jmq1p
            references device_command,
    propvalue        varchar(255),
    propkey          varchar(255) not null,
    constraint device_command_metadata_pkey
        primary key (devicecommand_id, propkey)
);

alter table device_command_metadata
    owner to sitewhere;

create table device_element_mapping
(
    id                      uuid not null
        constraint device_element_mapping_pkey
            primary key,
    deviceelementschemapath varchar(255),
    devicetoken             varchar(255)
);

alter table device_element_mapping
    owner to sitewhere;

create table device_device_element_mapping
(
    device_id                uuid not null
        constraint fkoehheg51b9yqvswagjpnirq30
            references device,
    deviceelementmappings_id uuid not null
        constraint uk_b1oeavb72q3dljj4r9olpkyqw
            unique
        constraint fkagc31b2yxvwybcn4nwe0fq2ya
            references device_element_mapping
);

alter table device_device_element_mapping
    owner to sitewhere;

create table device_element_schema
(
    id   uuid not null
        constraint device_element_schema_pkey
            primary key,
    name varchar(255),
    path varchar(255)
);

alter table device_element_schema
    owner to sitewhere;

create table device_element_schema_device_alot
(
    deviceelementschema_id uuid not null
        constraint fk7ue2a18b4xs7d34yv7fr1jddv
            references device_element_schema,
    deviceslots_id         uuid not null
        constraint uk_66ju75nl4nhu4jbq6g30xoxb8
            unique
        constraint fkk1pe4jyirkgfqapfcwxmsemj3
            references device_alot
);

alter table device_element_schema_device_alot
    owner to sitewhere;

create table device_group
(
    id              uuid not null
        constraint device_group_pkey
            primary key,
    backgroundcolor varchar(255),
    bordercolor     varchar(255),
    createdby       varchar(255),
    createddate     timestamp,
    description     varchar(255),
    foregroundcolor varchar(255),
    icon            varchar(255),
    imageurl        varchar(255),
    name            varchar(255),
    token           varchar(255),
    updatedby       varchar(255),
    updateddate     timestamp
);

alter table device_group
    owner to sitewhere;

create table device_group_element
(
    id            uuid not null
        constraint device_group_element_pkey
            primary key,
    deviceid      uuid,
    groupid       uuid,
    nestedgroupid uuid
);

alter table device_group_element
    owner to sitewhere;

create table device_group_metadata
(
    devicegroup_id uuid         not null
        constraint fkagh6eu89prqlut8wgrd7ru104
            references device_group,
    propvalue      varchar(255),
    propkey        varchar(255) not null,
    constraint device_group_metadata_pkey
        primary key (devicegroup_id, propkey)
);

alter table device_group_metadata
    owner to sitewhere;

create table device_metadata
(
    device_id uuid         not null
        constraint fklvnhinwxcopir2afw4hhd21dv
            references device,
    propvalue varchar(255),
    propkey   varchar(255) not null,
    constraint device_metadata_pkey
        primary key (device_id, propkey)
);

alter table device_metadata
    owner to sitewhere;

create table device_status
(
    id              uuid not null
        constraint device_status_pkey
            primary key,
    backgroundcolor varchar(255),
    bordercolor     varchar(255),
    code            varchar(255),
    createdby       varchar(255),
    createddate     timestamp,
    devicetypeid    uuid,
    devicetypetoken varchar(255),
    foregroundcolor varchar(255),
    icon            varchar(255),
    name            varchar(255),
    token           varchar(255),
    updatedby       varchar(255),
    updateddate     timestamp
);

alter table device_status
    owner to sitewhere;

create table device_status_metadata
(
    devicestatus_id uuid         not null
        constraint fk6hgkc6l5eh6j8d31tg2r1jb5g
            references device_status,
    propvalue       varchar(255),
    propkey         varchar(255) not null,
    constraint device_status_metadata_pkey
        primary key (devicestatus_id, propkey)
);

alter table device_status_metadata
    owner to sitewhere;

create table device_type
(
    id                  uuid not null
        constraint device_type_pkey
            primary key,
    backgroundcolor     varchar(255),
    bordercolor         varchar(255),
    containerpolicy     varchar(255),
    createdby           varchar(255),
    createddate         timestamp,
    description         varchar(1000),
    deviceelementschema bytea,
    foregroundcolor     varchar(255),
    icon                varchar(255),
    imageurl            varchar(255),
    name                varchar(255),
    token               varchar(255),
    updatedby           varchar(255),
    updateddate         timestamp
);

alter table device_type
    owner to sitewhere;

create table device_type_metadata
(
    devicetype_id uuid         not null
        constraint fk2l7wclaxgtbmwulpn4doah587
            references device_type,
    propvalue     varchar(255),
    propkey       varchar(255) not null,
    constraint device_type_metadata_pkey
        primary key (devicetype_id, propkey)
);

alter table device_type_metadata
    owner to sitewhere;

create table device_util
(
    id   uuid not null
        constraint device_util_pkey
            primary key,
    name varchar(255),
    path varchar(255)
);

alter table device_util
    owner to sitewhere;

create table device_element_schema_device_util
(
    deviceelementschema_id uuid not null
        constraint fk1wook5bkpddnrwmbrd9nocbbl
            references device_element_schema,
    deviceunits_id         uuid not null
        constraint uk_hnftlrnn5kik7meruwvjx6yxe
            unique
        constraint fksgst3d9r50s349o1qwdvidaoh
            references device_util
);

alter table device_element_schema_device_util
    owner to sitewhere;

create table device_util_device_alot
(
    deviceunit_id  uuid not null
        constraint fkjydms88on2qfubdjtu9at3k1c
            references device_util,
    deviceslots_id uuid not null
        constraint uk_nnbdw0stj9imql0cg1eqkynas
            unique
        constraint fkf84c6jkugpviwqk9j6fcpy1cf
            references device_alot
);

alter table device_util_device_alot
    owner to sitewhere;

create table device_util_device_util
(
    deviceunit_id  uuid not null
        constraint fkt566meix9u7sej4qdv9kl3ink
            references device_util,
    deviceunits_id uuid not null
        constraint uk_596eqj3seagx1ub70fjr8b2hb
            unique
        constraint fkk1b7dtas8igjw0ldujgmn6c75
            references device_util
);

alter table device_util_device_util
    owner to sitewhere;

create table devicegroup_roles
(
    devicegroup_id uuid not null
        constraint fkautwqvnlkybd7neyphvpmvsv7
            references device_group,
    roles          varchar(255)
);

alter table devicegroup_roles
    owner to sitewhere;

create table devicegroupelement_roles
(
    devicegroupelement_id uuid not null
        constraint fkrhhirutvofaadogo9i22n6wm
            references device_group_element,
    roles                 varchar(255)
);

alter table devicegroupelement_roles
    owner to sitewhere;

create table location
(
    id        uuid not null
        constraint location_pkey
            primary key,
    elevation double precision,
    latitude  double precision,
    longitude double precision
);

alter table location
    owner to sitewhere;

create table area_location
(
    area_id   uuid not null
        constraint fkf9vitu38uxi8v0xg584nhb8py
            references area,
    bounds_id uuid not null
        constraint uk_9u8gut07jhp8nik3ib8vo34aa
            unique
        constraint fkpmee0yxmo827vb8q6lcbvym38
            references location
);

alter table area_location
    owner to sitewhere;

create table zone
(
    id          uuid not null
        constraint zone_pkey
            primary key,
    areaid      uuid,
    bordercolor varchar(255),
    createdby   varchar(255),
    createddate timestamp,
    fillcolor   varchar(255),
    name        varchar(255),
    opacity     double precision,
    token       varchar(255),
    updatedby   varchar(255),
    updateddate timestamp
);

alter table zone
    owner to sitewhere;

create table zone_location
(
    zone_id   uuid not null
        constraint fkeml27magrfxvg7u29w8upfol5
            references zone,
    bounds_id uuid not null
        constraint uk_p6g9s7qtpydk3jbmq4t3gm002
            unique
        constraint fk4an113j8ldfqo7qu0t6f9no94
            references location
);

alter table zone_location
    owner to sitewhere;

create table zone_metadata
(
    zone_id   uuid         not null
        constraint fkemse0xhpumauen49j0jnllp21
            references zone,
    propvalue varchar(255),
    propkey   varchar(255) not null,
    constraint zone_metadata_pkey
        primary key (zone_id, propkey)
);

alter table zone_metadata
    owner to sitewhere;


-- Asset Management
create table asset
(
    id              uuid not null
        constraint asset_pkey
            primary key,
    assettypeid      uuid,
    backgroundcolor varchar(255),
    bordercolor     varchar(255),
    createdby       varchar(255),
    createddate     timestamp,
    description     varchar(1000),
    foregroundcolor varchar(255),
    icon            varchar(255),
    imageurl        varchar(255),
    name            varchar(255),
    parentid        uuid,
    token           varchar(255),
    updatedby       varchar(255),
    updateddate     timestamp
);

alter table asset
    owner to sitewhere;

create table asett_metadata
(
    asset_id   uuid         not null
        constraint fkhmfvnp287p256qi7bkg5dew6u
            references asset,
    propvalue varchar(255),
    propkey   varchar(255) not null,
    constraint asset_metadata_pkey
        primary key (asset_id, propkey)
);

create table asset_type
(
    id              uuid not null
        constraint asset_type_pkey
            primary key,
    backgroundcolor varchar(255),
    bordercolor     varchar(255),
    createdby       varchar(255),
    createddate     timestamp,
    description     varchar(1000),
    foregroundcolor varchar(255),
    icon            varchar(255),
    imageurl        varchar(255),
    name            varchar(255),
    token           varchar(255),
    updatedby       varchar(255),
    updateddate     timestamp,
    assetcategory   varchar(255)
);

alter table asset_type
    owner to sitewhere;

create table asett_type_metadata
(
    assettype_id uuid         not null
        constraint fk13869vnjm3fh73tfou4sr7onb
            references asset_type,
    propvalue   varchar(255),
    propkey     varchar(255) not null,
    constraint asset_type_metadata_pkey
        primary key (assettype_id, propkey)
);


-- Batch operations
create table batch_element
(
    id  uuid not null
        constraint batch_element_pkey
            primary key,
    batchoperationid uuid,
    deviceid      uuid,
    processingStatus varchar (255),
    processedDate     timestamp
);

alter table batch_element
    owner to sitewhere;

create table batch_element_metadata
(
    batch_element_id uuid         not null
        constraint fk13869vnjm3fh77tfou4sr7onb
            references batch_element,
    propvalue   varchar(255),
    propkey     varchar(255) not null,
    constraint batch_element_metadata_pkey
        primary key (batch_element_id, propkey)
);

create table batch_operation
(
    id  uuid not null constraint batch_operation_pkey primary key,
    token varchar (255),
    operation_type varchar (255),
    processing_status varchar (255),
    processing_started_date     timestamp,
    processing_ended_date     timestamp,
    created_date     timestamp,
    created_by     varchar (255),
    updated_date     timestamp,
    updated_by     varchar (255)
);

alter table batch_operation
    owner to sitewhere;

create table batch_operation_metadata
(
    batch_operation_id uuid         not null
        constraint fk13869vnpp3fh77tfou4sr7onb
            references batch_operation,
    propvalue   varchar(255),
    propkey     varchar(255) not null,
    constraint batch_operation_metadata_pkey
        primary key (batch_operation_id, propkey)
);

create table batch_operation_parameters
(
    batch_operation_parameters_id uuid         not null
        constraint fk13869vnpp3fh77tfoop4sr7onb
            references batch_operation,
    propvalue   varchar(255),
    propkey     varchar(255) not null,
    constraint batch_operation_parameters_pkey
        primary key (batch_operation_parameters_id, propkey)
);

-- Schedule Management
create table schedule
(
    id  uuid not null constraint schedule_pkey primary key,
    name varchar,
    token varchar,
    start_date timestamp,
    end_date timestamp,
    created_date timestamp,
    created_by varchar,
    updated_date timestamp,
    updated_by varchar,
    trigger_type varchar
);

alter table schedule
    owner to sitewhere;

create table schedule_metadata
(
    schedule_id uuid         not null
        constraint fk13869vnpp3fh77t23fou4sr7o
            references schedule,
    propvalue   varchar,
    propkey     varchar not null,
    constraint schedule_metadata_pkey
        primary key (schedule_id, propkey)
);

alter table schedule_metadata
    owner to sitewhere;

create table trigger_configuration
(
    schedule_id uuid         not null
        constraint fk13869vnpp3few7t23fou4sr7o
            references schedule,
    propvalue   varchar,
    propkey     varchar not null,
    constraint trigger_configuration_pkey
        primary key (schedule_id, propkey)
);

alter table trigger_configuration
    owner to sitewhere;

create table scheduled_job
(
    id  uuid not null constraint scheduled_job_pkey primary key,
    name varchar,
    token varchar,
    schedule_token varchar,
    start_date timestamp,
    end_date timestamp,
    created_date timestamp,
    created_by varchar,
    updated_date timestamp,
    updated_by varchar,
    job_type varchar,
    job_state varchar
);

alter table scheduled_job
    owner to sitewhere;


alter table schedule
    owner to sitewhere;

create table scheduled_job_metadata
(
    schedule_job_id uuid         not null
        constraint fk13169vnpp3fh77t23fou4sr7o
            references scheduled_job,
    propvalue   varchar,
    propkey     varchar not null,
    constraint schedule_job_metadata_pkey
        primary key (schedule_job_id, propkey)
);

alter table scheduled_job_metadata
    owner to sitewhere;

create table scheduled_job_configuration
(
    schedule_job_id uuid         not null
        constraint flu3169vnpp3fh77t23fou4sr7o
            references scheduled_job,
    propvalue   varchar,
    propkey     varchar not null,
    constraint schedule_job_configuration_pkey
        primary key (schedule_job_id, propkey)
);

alter table scheduled_job_configuration
    owner to sitewhere;