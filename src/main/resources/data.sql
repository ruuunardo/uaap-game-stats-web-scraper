insert into uaap_univ(univ_code, name_univ)
values("FEU","Far Eastern University"),
("NU","National University"),
("UP","University of the Philippines"),
("UST","University of Santo Tomas"),
("ADU","Adamson University"),
("UE","University of the East"),
("ADMU","Ateneo de Manila University"),
("DLSU","De La Salle University")
;

insert into users
values("ra", "{noop}123", 1)
,("re", "{noop}123", 1)
,("ru", "{noop}123", 1)
,("admin", "{noop}admin", 1)
;

insert into roles
values("ra", "ROLE_USER")
,("re", "ROLE_USER")
,("ru", "ROLE_ADMIN")
,("ru", "ROLE_USER")
,("admin", "ROLE_ADMIN")
,("admin", "ROLE_USER")
;
