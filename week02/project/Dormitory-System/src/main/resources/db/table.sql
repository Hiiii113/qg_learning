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
    image_url      VARCHAR(500)     null comment '问题描述图片路径',
    priority       tinyint unsigned not null default 2 comment '紧急程度 1-紧急 2-普通 3-低',
    status         tinyint unsigned not null default 1 comment '状态 1-待受理 2-处理中 3-已完成 4-已取消',
    staff_number   int unsigned              default null comment '维修人员id',
    rating         int unsigned              default null comment '评价等级',
    completed_time datetime                  default null comment '完成时间',
    create_time    datetime         not null default current_timestamp comment '创建时间',
    update_time    datetime         not null default current_timestamp on update current_timestamp comment '更新时间'
) comment '维修保单表';

-- 角色表
create table sys_role
(
    id        int primary key auto_increment comment '主键',
    role_name varchar(50) not null comment '角色名称（例如：管理员）',
    role_code varchar(50) not null comment '角色标识（例如：admin）'
) comment '角色表';

-- 权限表
create table sys_permission
(
    id              int primary key auto_increment comment '主键',
    permission_name varchar(50) not null comment '权限名称（例如：删除报修单）',
    permission_code varchar(50) not null comment '权限标识'
) comment '权限表';

-- 用户-角色关联表
create table sys_user_role
(
    user_id int not null comment '用户id',
    role_id int not null comment '角色id',
    primary key (user_id, role_id)
) comment '用户-角色关联表';

-- 角色-权限关联表
create table sys_role_permission
(
    role_id       int not null comment '角色id',
    permission_id int not null comment '权限id',
    primary key (role_id, permission_id)
) comment '角色-权限关联表';

-- 插入角色
INSERT INTO sys_role (id, role_name, role_code)
VALUES (1, '普通用户', 'user'),
       (2, '管理员', 'admin');

-- 插入权限点
INSERT INTO sys_permission (id, permission_name, permission_code)
VALUES (1, '查看个人报修单', 'order:listMe'),
       (2, '查看所有报修单', 'order:listAll'),
       (3, '创建报修单', 'order:add'),
       (4, '修改报修单', 'order:modify'),
       (5, '更新报修单', 'order:update'),
       (6, '删除报修单', 'order:delete'),
       (7, '获取系统日志', 'log:list');

-- 关联角色与权限 (管理员)
INSERT INTO sys_role_permission (role_id, permission_id)
VALUES (2, 1),
       (2, 2),
       (2, 3),
       (2, 4),
       (2, 5),
       (2, 6),
       (2, 7);
-- 管理员(2)拥有所有权限

-- 关联角色与权限(普通用户)
INSERT INTO sys_role_permission (role_id, permission_id)
VALUES (1, 1),
       (1, 3),
       (1, 4);

-- 日志表
CREATE TABLE `sys_operation_log`
(
    id             int          NOT NULL AUTO_INCREMENT COMMENT '日志ID（自增主键）',
    user_number    varchar(50)  NOT NULL COMMENT '操作用户',
    operation_time datetime     NOT NULL COMMENT '操作时间',
    module         varchar(100) NOT NULL COMMENT '操作模块（如：用户管理）',
    operator       varchar(255)          DEFAULT NULL COMMENT '操作描述（如：新增用户）',
    method         varchar(255) NOT NULL COMMENT '操作方法全路径',
    params         text COMMENT '方法参数（JSON格式）',
    result         text COMMENT '操作结果（成功/失败，JSON格式）',
    exception      text COMMENT '异常信息（失败时记录）',
    cost_time      bigint                DEFAULT NULL COMMENT '操作耗时（毫秒）',
    client_ip      varchar(50)           DEFAULT NULL COMMENT '客户端IP',
    create_time    datetime     not null default current_timestamp comment '创建时间',
    PRIMARY KEY (id),
    KEY idx_operation_time (operation_time) COMMENT '按操作时间查询索引',
    KEY idx_username (user_number) COMMENT '按用户查询索引'
) COMMENT ='系统操作日志表';