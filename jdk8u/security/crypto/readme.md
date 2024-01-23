##### 描述
本项目用于测试`AES/RSA/HMAC/EC`等算法的性能。

##### 如何构建
将通过以下命令在目录中生成：`target/benchmarks.jar`
```java
mvn install
```

##### 如何运行
使用命令运行`benchmarks.jar`
```java
java -jar target/benchmarks.jar
```

##### 运行结果
| Benchmark  | (algorithm)  | (dataSize) | (keySize)  |  Mode |  Cnt | Score  | Error  | Units  |
| ------------ | ------------ | ------------ | ------------ | ------------ | ------------ | ------------ | ------------ | ------------ |
| AESBenchmark.decrypt   |  AES/ECB/PKCS5Padding  | 1024  | N/A  |  thrpt | 5  | 654042.275 ±  | 202355.594  | ops/s  |
| AESBenchmark.decrypt   |  AES/ECB/PKCS5Padding  | 1024  | N/A  |  thrpt |  5 | 654042.275 ±  | 202355.594  | ops/s  |
| AESBenchmark.decrypt   |  AES/ECB/PKCS5Padding  | 1024  | N/A  |  thrpt |  5 | 654042.275 ±  | 202355.594  | ops/s  |
| AESBenchmark.decrypt   |  AES/ECB/PKCS5Padding  | 1024  | N/A  |  thrpt |  5 |  654042.275 ± |  202355.594 |  ops/s |
|  AESBenchmark.decrypt  |   AES/ECB/PKCS5Padding | 1024  | N/A  |  thrpt |  5 |  654042.275 ± | 202355.594  |  ops/s |

Benchmark:  表示用例场景：数据签名、密钥生成、数据加解密等<br>
Algorithm：使用的加解密算法<br>
dataSize：待加解密数据长度<br>
Score：性能跑分，代表每秒执行多少次函数调用，理论数值性能越高代表性能越好<br>