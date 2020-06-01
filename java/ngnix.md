# <span style="text-align:center;">Ngnix</span>

[官网](https://docs.nginx.com/nginx/admin-guide)



## Ngnix简介

## 1.正向代理



## 2.反向代理

### 2.1 根据server_name配置转发

```
server {
        listen       80;
        server_name  localhost;

        location / {
            proxy_pass http://127.0.0.1:8080;
            index  index.html index.htm index.jsp;
        }
    }
```

### 2.2 根据IP地址转发

```
server {
        listen       80;
        server_name  192.168.1.1;

        location / {
            proxy_pass http://192.168.1.101:8080;
            index  index.html index.htm index.jsp;
        }
    }
```



### 2.3 根据location路径转发

该指令用于匹配 URL。

语法 ：

```
location [ = | ~ | ~* | ^~] uri {

}
```

　1、= ：用于不含正则表达式的 uri 前，要求请求字符串与 uri 严格匹配，如果匹配成功，就停止继续向下搜索并立即处理该请求。

　2、~：用于表示 uri 包含正则表达式，并且区分大小写。

　3、~*：用于表示 uri 包含正则表达式，并且不区分大小写。

　4、^~：用于不含正则表达式的 uri 前，要求 Nginx 服务器找到标识 uri 和请求字符串匹配度最高的 location 后，立即使用此 location 处理请求，而不再使用 location 块中的正则 uri 和请求字符串做匹配。

　　注意：如果 uri 包含正则表达式，则必须要有 ~ 或者 ~* 标识。

配置

```
server {
        listen       80;
        server_name  192.168.1.1;

        location / {
            proxy_pass http://192.168.1.101:8080;
            index  index.html index.htm index.jsp;
        }
    }
```

### 2.4 proxy_pass

该指令用于设置被代理服务器的地址。可以是主机名称、IP地址加端口号的形式。

```
proxy_pass  http://www.123.com/uri;
```

### 2.5 index

该指令用于设置网站的默认首页。后面的文件名称可以有多个，中间用空格隔开。

```
index  filename ...;
```

```
index  index.html index.jsp;
```



## 3.负载均衡

```
	http{
        upstram mylb{
            server ip:port1
            server ip:port2
        }
        server{
            server_name  ip;
            proxy_pass	http://mylb
        }
      }

}
```

## 选择负载均衡方法：

NGINX开源支持四种负载平衡方法，而NGINX Plus又增加了两种方法：

https://docs.nginx.com/nginx/admin-guide/load-balancer/http-load-balancer/#weights

1. **Round Robin** –请求在服务器之间平均分配，同时考虑了[服务器权重](https://docs.nginx.com/nginx/admin-guide/load-balancer/http-load-balancer/#weights)。默认情况下使用此方法（没有启用它的指令）：

	```
	upstram mylb{
	＃默认Round Robin 
	server ip:port1
	server ip:port2
	}
	```

2. [**最少的连接**](https://nginx.org/en/docs/http/ngx_http_upstream_module.html#least_conn) -将活动连接最少的请求发送到服务器，再次考虑[服务器权重](https://docs.nginx.com/nginx/admin-guide/load-balancer/http-load-balancer/#weights)：

	```
	upstream backend {
	    least_conn;
	    server backend1.example.com;
	    server backend2.example.com;
	}
	```

	

3. [**IP哈希**](https://nginx.org/en/docs/http/ngx_http_upstream_module.html#ip_hash) -从客户端IP地址确定向其发送请求的服务器。在这种情况下，可以使用IPv4地址的前三个八位位组或整个IPv6地址来计算哈希值。该方法保证了来自同一地址的请求将到达同一服务器，除非它不可用。

	```
	upstream backend {
	    ip_hash;
	    server backend1.example.com;
	    server backend2.example.com;
	}
	```

	

如果其中一台服务器需要暂时从负载平衡循环中删除，则可以使用[`down`](https://nginx.org/en/docs/http/ngx_http_upstream_module.html#down)参数对其进行标记，以保留客户端IP地址的当前哈希值。该服务器要处理的请求将自动发送到组中的下一个服务器：

```
upstream backend {
    server backend1.example.com;
    server backend2.example.com;
    server backend3.example.com down;
}
```



4. 通用[**哈希**](https://nginx.org/en/docs/http/ngx_http_upstream_module.html#hash) –向其发送请求的服务器是根据用户定义的键确定的，该键可以是文本字符串，变量或组合。例如，密钥可以是成对的源IP地址和端口，或者是本示例中的URI：

	```
	upstream backend {
	    hash $request_uri consistent;
	    server backend1.example.com;
	    server backend2.example.com;
	}
	```

5. 最短[**时间**](https://nginx.org/en/docs/http/ngx_http_upstream_module.html#least_time)（仅NGINX Plus）–对于每个请求，NGINX Plus选择具有最低平均延迟和最低活动连接数的服务器，其中最低平均延迟是根据包含在指令中以下[参数](https://nginx.org/en/docs/http/ngx_http_upstream_module.html#least_time)中的哪个来计算的`least_time`：

  - `header` –从服务器接收第一个字节的时间
  - `last_byte` –是时候从服务器接收完整响应了
  - `last_byte inflight` –考虑到请求不完整的时间，可以从服务器接收完整的响应

  ```
  upstream backend {
   least_time header;
      server backend1.example.com;
      server backend2.example.com;
  }
  ```

  

6. [**随机**](https://nginx.org/en/docs/http/ngx_http_upstream_module.html#random) –每个请求将传递到随机选择的服务器。如果`two`指定了参数，首先，NGINX考虑服务器权重随机选择两个服务器，然后使用指定的方法选择这些服务器之一：

	- `least_conn` –活动连接最少
	- `least_time=header`（NGINX Plus）–从服务器接收响应标头的最短平均时间（[`$upstream_header_time`](https://nginx.org/en/docs/http/ngx_http_upstream_module.html#var_upstream_header_time)）
	- `least_time=last_byte`（NGINX Plus）–从服务器接收完整响应的最短平均时间（[`$upstream_response_time`](https://nginx.org/en/docs/http/ngx_http_upstream_module.html#var_upstream_response_time)）

	```
	upstream backend {
	    random two least_time=last_byte;
	    server backend1.example.com;
	    server backend2.example.com;
	    server backend3.example.com;
	    server backend4.example.com;
	}
	```

	

## 4.动静分离

```
location /访问静态资源文件夹/{
	root 静态资源路径上级目录
}

location /resources/ {
	root /resources_parent/
}

location /img/ {
	root /static/
	autoindex on #访问根目录时列出所有文件
}
```



## 5.HA-keepalived

设置ngnix主从配置



## 6.常用命令

重新加载ngnix

```shell 
ngnix -s reload
```

