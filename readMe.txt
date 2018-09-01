springmvc-test1：
	自定义HandlerMethodArgumentResolver，使得从页面传进来的user.id, user.name这些字段自动赋值到user对象中
	例如：
		在addUser(@FormBean("user") User user, @FormBean("role") Role role)方法中
		输入：http://localhost:8080/user/addUser?user.id=111&user.name=AAA&user.type=type1&role.id=222&role.name=BBB
		输出：User [id=111, name=AAA, type=type1]  Role [id=222, name=BBB]

