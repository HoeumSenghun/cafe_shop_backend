
insert into roles(name) values ('ADMIN') on conflict (name) do nothing;
insert into roles(name) values ('CASHIER') on conflict (name) do nothing;
insert into roles(name) values ('CUSTOMER') on conflict (name) do nothing;

