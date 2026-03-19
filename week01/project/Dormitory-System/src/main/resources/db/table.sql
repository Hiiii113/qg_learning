-- 创建user表
create table user
(
    id          int unsigned     not null auto_increment primary key comment 'ID，用户唯一标识',
    user_number varchar(20)      not null unique comment '学号',
    username    varchar(20)      null comment '姓名',
    password    varchar(100)     not null comment '密码（加密后）',
    role        tinyint unsigned not null default 1 comment '身份: 1-学生 2-维修人员',
    dorm_room   varchar(50)      null comment '宿舍地址',
    create_time datetime         not null default current_timestamp comment '创建时间',
    update_time datetime         not null default current_timestamp on update current_timestamp comment '更新时间'
) comment '用户信息表';

-- 创建报修单
create table repair_order
(
    id             int unsigned     not null auto_increment primary key comment 'id',
    user_id        int unsigned     not null comment '报修人的id',
    user_number    varchar(20)      null comment '报修人的学号',
    dorm_room      varchar(50)      null comment '报修人的宿舍地址',
    problem        text             not null comment '问题描述',
    priority       tinyint unsigned not null default 2 comment '紧急程度 1-紧急 2-普通 3-低',
    status         tinyint unsigned not null default 1 comment '状态 1-待受理 2-处理中 3-已完成 4-已取消',
    staff_id       int unsigned              default null comment '维修人员id',
    staff_name     varchar(20)               default null comment '维修人员姓名',
    completed_time datetime                  default null comment '完成时间',
    create_time    datetime         not null default current_timestamp comment '创建时间',
    update_time    datetime         not null default current_timestamp on update current_timestamp comment '更新时间'
) comment '维修保单表';