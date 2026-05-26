

## Kịch bản thực tế

Giả sử sản phẩm X hiện đang có tồn kho là 8 sản phẩm.

* Người dùng A thêm sản phẩm X vào giỏ hàng với số lượng 5.
* Lúc này hệ thống chưa trừ tồn kho thật trong database, mà chỉ lưu sản phẩm trong giỏ hàng.
* Sau đó, người dùng B mua thành công 3 sản phẩm X.
* Tồn kho thực tế trong hệ thống lúc này chỉ còn 5 sản phẩm.
* Tiếp theo, người dùng A quay lại giỏ hàng và cập nhật số lượng sản phẩm X từ 5 lên 7.

Trong trường hợp này, hệ thống cần kiểm tra lại tồn kho hiện tại của sản phẩm trước khi cho phép cập nhật giỏ hàng.



# Các vấn đề có thể xảy ra nếu ShoppingCartService xử lý không chặt chẽ

## 1. Không kiểm tra lại tồn kho mới nhất

Nếu service chỉ dựa vào số lượng cũ khi người dùng thêm vào giỏ mà không kiểm tra lại dữ liệu mới từ ProductRepository, hệ thống có thể cho phép cập nhật lên 7 sản phẩm dù kho hiện tại chỉ còn 5.

Điều này dẫn đến:

* Overselling (bán vượt tồn kho)
* Dữ liệu đơn hàng sai
* Khách hàng đặt được sản phẩm nhưng không thể giao hàng

Trong trường hợp này nên ném ra:

```java
IllegalStateException
```

với thông báo:

```java
Not enough stock for product
```


## 2. Dữ liệu giữa Product và ShoppingCart không đồng bộ

Một sản phẩm có thể đã:

* bị xóa khỏi hệ thống
* ngừng kinh doanh
* thay đổi thông tin tồn kho

nhưng vẫn còn nằm trong giỏ hàng của người dùng.

Nếu ShoppingCartService không kiểm tra lại ProductRepository trước khi cập nhật hoặc thanh toán, hệ thống có thể:

* phát sinh NullPointerException
* cập nhật giỏ hàng sai
* hiển thị thông tin không chính xác cho người dùng

Trong trường hợp này nên ném:

```java
IllegalArgumentException
```

với thông báo:

```java
Product not found
```


## 3. Cho phép số lượng không hợp lệ

Người dùng có thể cập nhật:

* số lượng bằng 0
* số lượng âm

Nếu service không validate dữ liệu đầu vào, giỏ hàng sẽ chứa dữ liệu không hợp lệ và làm sai logic nghiệp vụ.

Ví dụ:

* tổng tiền bị âm
* số lượng sản phẩm sai
* lỗi khi thanh toán

Trong trường hợp này nên ném:

```java
IllegalArgumentException
```

với thông báo:

```java
Quantity must be positive
```


## 4. Lỗi khi nhiều người dùng thao tác cùng lúc

Trong môi trường thương mại điện tử thực tế, nhiều người dùng có thể cùng mua một sản phẩm tại cùng thời điểm.

Nếu hệ thống:

* không kiểm tra tồn kho ở thời điểm mới nhất
* không đồng bộ dữ liệu đúng cách

thì có thể xảy ra:

* race condition
* cập nhật tồn kho sai
* nhiều người cùng mua vượt quá số lượng tồn kho thật

Đây là lỗi rất nghiêm trọng vì ảnh hưởng trực tiếp đến:

* đơn hàng
* doanh thu
* trải nghiệm người dùng



# Kết luận

ShoppingCartService cần được thiết kế và kiểm thử kỹ lưỡng để đảm bảo:

* luôn kiểm tra tồn kho mới nhất
* validate dữ liệu đầu vào
* xử lý đúng khi sản phẩm không tồn tại
* đảm bảo dữ liệu giỏ hàng luôn đồng bộ với dữ liệu sản phẩm

Nếu không, hệ thống rất dễ gặp lỗi logic, bán vượt tồn kho hoặc làm sai dữ liệu đơn hàng trong môi trường nhiều người dùng hoạt động đồng thời.
