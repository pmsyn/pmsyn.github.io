# python学习注意事项
1、python 环境设置
	#!/usr/bin/env python3
	# -*- coding: coding -*-
	
2、数学运算
	3/2 = 1.5 返回float
	3//2 = 2 丢弃小数点
3、字符串输出
	3.1 防止特殊符号转义+r
		print(r'\name') 输出：\name
	3.2 字符串文字跨越多行。使用三引号： """..."""或'''...'''
	3.3 字符串通过+连接，重复显示多个相同字符使用*
		print(2*'abc'+'cd') 输出：abcabccd
	3.4 两个或多个彼此相邻的字符串文字（即引号之间的字符串）会自动连接，如果要连接变量或变量和文字使用+
		print('py'  'thon') 输出：python
		pre = py
		print('python:'+pre+'thon') ==> python:python
4、定义函数
	1、默认参数
		def ask_ok(prompt, retries=4, reminder='Please try again!')
	2、*name,**name *name必须在**name前面，*name [](list)参数，**name{}(集合)形式
		def cheeseshop(kind, *arguments, **keywords):
		调用
		cheeseshop('kind','a','b')
		cheeseshop('kind','a','b',k='v')
	3.lamda表达式 返回函数
		def make_incrementor(n):
			return lambda x: x + n
		f = make_incrementor(42)
		f(0)
		42
		f(1)
		43
5.导入子模块或函数
	1、包的用户可以从包中导入单个模块，例如：import sound.effects.echo
	2、导入子模块的另一种方法是： from sound.effects import echo
	
	导入子模块中的函数或变量：
	from sound.effects.echo import echofilter
6.输出变量
	1.使用{}
	year = 2016 ; event = 'Referendum'
	>>> f'Results of the {year} {event}'
	>>> 'Results of the 2016 Referendum'
7.读写文件
	open()返回一个文件对象，最常用的有两个参数：。open(filename, mode)
	f = open('workfile', 'w')
	第一个参数是包含文件名的字符串。第二个参数模式可以是：
	'r'仅读取文件时，
	'w' 仅写入（将删除同名的现有文件），
	'a'打开文件进行追加; 写入文件的任何数据都会自动添加到最后。 
	'r+'打开文件进行读写
	'b'附加到模式后以二进制模式打开文件
8.yield返回数据	
	def reverse(data):
		for index in range(len(data)-1, -1, -1):
			yield data[index]
9、日志
	import logging
	logging.debug('Debugging information')
	logging.info('Informational message')


	
