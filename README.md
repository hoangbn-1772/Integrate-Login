# Integrate-Login
Integrate Facebook, Twitter, Google for login

# An Introduction to OAuth 2
- OAuth2 là một framework ủy quyền, cho phép các ứng dụng có quyền truy cập hạn chế vào tài khoản người dùng trên dịch vụ HTTP như là Facebook, Github, DigitalOcean.
- Nó hoạt động bằng cách ủy quyền xác thực người dùng cho bên thứ 3. Bên thứ 3 có thể truy cập vào tài khoản người dùng.
- OAuth định nghĩa 4 vai trò.
  + Resource Owner (User): Là người dùng cho phép **application** truy cập vào tài khoản của họ. Quyền truy cập vào tài khoản của người dùng bị giới hạn trong phạm vi được cấp (quyền truy cập đọc hoặc ghi)
  + Client (Application): Là ứng dụng muốn truy cập vào tài khoản của người dùng. Cần phải được người dùng ủy quyền và được xác thực bởi API.
  + Resource Server (API): Là nơi lưu trữ các tài khoản người dùng được bảo vệ.
  + Authorization Server (API): Xác minh danh tính của người dùng sau đó cung cấp **token** truy cập vào ứng dụng.
- Sơ đồ luông giao thức:
<img src="https://assets.digitalocean.com/articles/oauth/abstract_flow.png" alt="Abstract Protocol Flow">

## Application Registration
- Trước khi sử dụng OAuth trong ứng dụng, bạn phải đăng ký ứng dụng của mình với nhà cung cấp dịch vụ (Facebook, Google...) bằng cách sử dụng **API** hoặc thông qua form đăng ký trong phần **developer**. Bạn sẽ cung cấp các thông tin như: Application Name, Application Website, Redirect URI or Callback URL...
- Sau khi đăng ký thành công, dịch vụ sẽ cung cấp **client credentials** dưới hình thức một **client identifier** (Client ID) và một **client secret**.
  + **client identifier** là chuỗi công khai được API sử dụng để xác định ứng dụng và để xây dựng các URL ủy quyền được trình bày tới người dùng.
  + **client secret** được giữ bí mật giữa ứng dụng và API, dùng để xác thực định danh của ứng dụng với API khi ứng dụng yêu cầu truy cập vào user account.
