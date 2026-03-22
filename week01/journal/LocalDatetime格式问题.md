# LocalDatetime格式问题

在数据库中使用 datetime 存储时间，并使用 LocalDateTime 加载到后端并返回前端时，格式是这样的：

`2026-03-19T13:36:11`

中间有一个难看的 T 隔开了，前端处理很麻烦，可以在 entity 层的 LocalDateTime 上加上

`@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")`

就可以更改这个字段的格式了