
create table if not exists roles (
  id bigserial primary key,
  name varchar(32) not null unique
);

create table if not exists users (
  id bigserial primary key,
  email varchar(120) not null unique,
  full_name varchar(120) not null,
  password_hash text not null,
  enabled boolean not null default true,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create table if not exists user_roles (
  user_id bigint not null,
  role_id bigint not null,
  primary key (user_id, role_id),
  constraint fk_user_roles_user
    foreign key (user_id) references users(id) on delete cascade,
  constraint fk_user_roles_role
    foreign key (role_id) references roles(id) on delete restrict
);


create index if not exists idx_users_email on users(email);
create index if not exists idx_user_roles_role_id on user_roles(role_id);

create table if not exists products (
  id bigserial primary key,
  name varchar(160) not null,
  description varchar(2000),
  price numeric(12,2) not null,
  category varchar(80) not null,
  image_url varchar(500),
  is_available boolean not null default true,
  is_deleted boolean not null default false,
  deleted_at timestamptz,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create index if not exists idx_products_name on products(name);
create index if not exists idx_products_category on products(category);
create index if not exists idx_products_available on products(is_available);
create index if not exists idx_products_deleted on products(is_deleted);

create table if not exists orders (
  id bigserial primary key,
  customer_id bigint not null,
  status varchar(20) not null,
  total_amount numeric(12,2) not null default 0,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  constraint fk_orders_customer
    foreign key (customer_id) references users(id) on delete restrict
);

create index if not exists idx_orders_customer_id on orders(customer_id);
create index if not exists idx_orders_status on orders(status);
create index if not exists idx_orders_created_at on orders(created_at);

create table if not exists order_items (
  id bigserial primary key,
  order_id bigint not null,
  product_id bigint not null,
  quantity int not null check (quantity >= 1),
  unit_price numeric(12,2) not null,
  line_total numeric(12,2) not null,
  constraint fk_order_items_order
    foreign key (order_id) references orders(id) on delete cascade,
  constraint fk_order_items_product
    foreign key (product_id) references products(id) on delete restrict
);

create index if not exists idx_order_items_order_id on order_items(order_id);
create index if not exists idx_order_items_product_id on order_items(product_id);

create table if not exists payments (
  id bigserial primary key,
  order_id bigint not null,
  amount numeric(12,2) not null,
  method varchar(10) not null,
  status varchar(10) not null,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  constraint fk_payments_order
    foreign key (order_id) references orders(id) on delete restrict
);

create index if not exists idx_payments_order_id on payments(order_id);
create index if not exists idx_payments_status on payments(status);

create table if not exists refresh_tokens (
  id bigserial primary key,
  token varchar(512) not null unique,
  user_id bigint not null,
  expires_at timestamptz not null,
  revoked boolean not null default false,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  constraint fk_refresh_tokens_user
    foreign key (user_id) references users(id) on delete cascade
);

create index if not exists idx_refresh_tokens_token on refresh_tokens(token);
create index if not exists idx_refresh_tokens_user_id on refresh_tokens(user_id);

