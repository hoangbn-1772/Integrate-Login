# Integrate-Login
- Đặt vấn đề: Giả sử bạn đang sử dụng khoảng 10 app trở lên, và mỗi app cần phải có tài khoản để đăng nhập. Vì vậy, việc ngồi đăng ký và ghi nhớ thông tin đăng nhập cho mỗi app thực sự là một thảm họa, đặc biệt hơn là mỗi app bạn lại đặt username và password khác nhau (omg).
- Thật may, nhận ra điều này các ông lớn như Facebook, Twitter, Google đã đàm phán với nhau và đưa ra chuẩn mới **Open Authentication**.

# Introduction OAuth2
- OAuth2 là một framework, một **phương thức chứng thực** giúp các ứng dụng có thể chia sẻ tài nguyên với nhau mà không cần chia sẻ thông tin đăng nhập (username, password).
- Từ **Auth** có 2 ý nghĩa:
  + Authentication: Xác thực người dùng thông qua việc đăng nhập.
  + Authorization: Cấp quyền truy cập vào các resource.
- Quay lại phần đặt vấn đề một chút, bây giờ bạn chỉ cần có một tài khoản Facebook/Google/Twitter là bạn có thể đăng nhập vào nhiều ứng dụng khác nhau mà không phải nhớ tài khoản nữa (awesome)

- OAuth2 làm việc với 4 đối tượng với các vai trò khác nhau:
  + Resource Owner (User): là chủ sở hữu dữ liệu muốn chia sẻ.
  + Client (Application): Là ứng dụng muốn truy cập vào dữ liệu của người dùng. Cần phải được người dùng ủy quyền và được xác thực bởi API (Facebook, Twitter, Google...)
  + Resource Server (API): là server chứa thông tin dữ liệu cần chia sẻ.
  + Authorization Server (API): Kiểm tra thông tin người dùng, sau đó cung cấp **token** để truy cập vào dữ liệu cho client
  
## OAuth2 hoạt động như thế nào?

<img src="https://assets.digitalocean.com/articles/oauth/abstract_flow.png" alt="Abstract Protocol Flow">

## Đăng ký thông tin cho ứng dụng
- Trước khi sử dụng OAuth, bạn phải đăng ký ứng dụng của mình với nhà cung cấp dịch vụ (Facebook, Google...). Bạn sẽ phải cung cấp các thông tin như:
  + Application Name: Tên ứng dụng
  + Application Website: Website của ứng dụng
  + Redirect URI or Callback URL: Địa chỉ quay lại khi quá trình ủy quyền hoàn tất
- Sau khi đăng ký thành công, dịch vụ sẽ cung cấp **client credentials** bao gồm các thông tin:
  + **client identifier** (Client ID): là chuỗi công khai được API sử dụng để xác định ứng dụng và để xây dựng các URL ủy quyền hiển thị phía user.
  + **client secret** được giữ bí mật giữa ứng dụng và API, dùng để xác thực định danh của ứng dụng với API khi ứng dụng yêu cầu truy cập thông tin tài khoản user.
- Các thông tin quản lý ứng dụng dạng như sau:
<img src="images/application_registration.png"/>

## Authorization Grant
- Theo như Flow trên, 4 bước đầu tiên sử dụng để lấy ủy quyền (authorization grant) và access token. **Authorization grant type** phụ thuộc vào phương thức sử dụng để yêu cầu ủy quyền. OAuth2 định nghĩa 4 loại grant type:
  + Authorization Code: Sử dụng với các ứng dụng phía Server
  + Implicit: Sử dụng với các Mobile App (ứng dụng chạy trên thiết bị của user) hoặc Web App (Browser App)
  + Resource Owner Password Credentials: Sử dụng với các ứng dụng đáng tin cậy, ví dụ như các ứng dụng do chính service sở hữu.
  + Client Credentials: Sử dụng vơí ứng dụng truy cập thông qua API.
### Grant type: Authorization Code
- Đây là loại cấp mã ủy quyền được sử dụng phổ biến nhất vì nó tối ưu hóa cho các ứng dụng phía server, nơi source code không công khai và **client secret** được duy trì bảo mật.
- Đây là luồng dựa trên chuyển hướng, nên ứng dụng phải có khả năng tương tác với tác nhân người dùng (user-agent) và nhận API authorization code thông qua user-agent.
- Flow:
<img src="https://assets.digitalocean.com/articles/oauth/auth_code_flow.png" alt="Authorization Code Flow">

  + Yêu cầu authorization code: Đầu tiên user được cung cấp một liên kết để nhận **authorization code** có dạng như sau:
  <pre class="code-pre "><code langs="">https://cloud.digitalocean.com/v1/oauth/authorize?response_type=code&amp;client_id=<span class="highlight">CLIENT_ID</span>&amp;redirect_uri=<span class="highlight">CALLBACK_URL</span>&amp;scope=<span class="highlight">read</span>
</code></pre>

  + Người dùng ủy quyền cho ứng dụng:
  <img src="https://assets.digitalocean.com/articles/oauth/authcode.png" alt="Authorization Code Link">
  
  + Application nhận Authorization Code: Khi user cho phép quyền truy cập, dịch vụ sẽ chuyển hướng người dùng đến URI(đã được đăng ký trước đó), Applicaiton sẽ lưu lại **authorization code**
  <pre class="code-pre "><code langs="">https://dropletbook.com/callback?code=<span class="highlight">AUTHORIZATION_CODE</span></code></pre>
  
  + Application yêu cầu Access Token:
  <pre class="code-pre "><code langs="">https://cloud.digitalocean.com/v1/oauth/token?client_id=<span class="highlight">CLIENT_ID</span>&amp;client_secret=<span class="highlight">CLIENT_SECRET</span>&amp;grant_type=authorization_code&amp;code=<span class="highlight">AUTHORIZATION_CODE</span>&amp;redirect_uri=<span class="highlight">CALLBACK_URL</span>
</code></pre>

  + Application nhận Access Token:
  <pre class="code-pre "><code langs="">{"access_token":"<span class="highlight">ACCESS_TOKEN</span>","token_type":"bearer","expires_in":2592000,"refresh_token":"<span class="highlight">REFRESH_TOKEN</span>","scope":"read","uid":100101,"info":{"name":"Mark E. Mark","email":"mark@thefunkybunch.com"}}
</code></pre>

### Grant type: Implicit
- Được sử dụng cho mobile apps và web application, nơi **client secret** không được bảo mật.
- Được coi như một **redirection-based flow** trong đó **access token** được đưa đến Application thông qua Browser. Do đó, phương thức ủy quyền này không xác thực ID của ứng dụng mà tin tưởng hoàn toàn vào **redirect URI**.
- Không support **refresh tokens**.
- Flow: Ta có thể hiểu đơn giản luồng hoạt động như sau: Application gửi yêu cầu ủy quyền đến User, sau đó Authorization Server truyền thằng access token đến Browser và sau đó truyền lại cho Application.

<img src="https://assets.digitalocean.com/articles/oauth/implicit_flow.png" alt="Implicit Flow">

  + User yêu cầu access_token: Tương tự Authorization Code, chỉ khác ở tham số response_type=token
  + User ủy quyền cho Application: Tương tự Authorization Code
  + Browser nhận access_token thông qua Redirect URI:
  <pre class="code-pre "><code langs="">https://dropletbook.com/callback#token=<span class="highlight">ACCESS_TOKEN</span>
</code></pre>

  + Browser duy trì access_token: Sau khi được chuyển hướng về **redirect_uri**, nhiệm vụ của Browser là duy trì **access_token**. Ngoài ra tùy từng Service sẽ có thêm những tham số khác để điều hướng quay trở lại ứng dụng.
  + Application trích xuất access_token: Từ URL trả về có chứa **access_token** nhiệm vụ phải thực hiện là trích xuất ra **access_token**
  + access_token được gửi đến Application: Sau khi lấy được **access_token** thì nó sẽ được chuyển về ứng dụng. Đến đây quá trình ủy quyền hoàn tất và ứng dụng có thể sử dụng **access_token**.

### Grant type: Resource Owner Password Credentials
- User cung cấp thông tin đăng nhập trực tiếp cho ứng dụng để lấy **access token** từ service.
- Chỉ nên sử dụng với các ứng dụng được người dùng tin cậy.
- Sau khi user cung cấp thông tin đăng nhập cho ứng dụng, ứng dụng sẽ yêu cầu **access token** từ authorization server.
<pre class="code-pre "><code langs="">https://oauth.example.com/token?grant_type=password&amp;username=<span class="highlight">USERNAME</span>&amp;password=<span class="highlight">PASSWORD</span>&amp;client_id=<span class="highlight">CLIENT_ID</span>
</code></pre>

### Grant type: Client Credentials
- Loại ủy quyền này phục vụ cho việc truy cập vào chính thông tin tài khoản của Application tại service. Ví dụ như Application muốn thay đổi thông tin description hoặc redirect_uri... User không tham gia vào quá trình ủy quyền.
- Flow: Ứng dụng yêu cầu **access token** bằng cách gửi thông tin đăng nhập (client ID và client secret) là có thể thực hiện quá trình ủy quyền:
<pre class="code-pre "><code langs="">https://oauth.example.com/token?grant_type=client_credentials&amp;client_id=CLIENT_ID&amp;client_secret=CLIENT_SECRET
</code></pre>

## Sử dụng Access Token
- Sau khi đã có **access_token**, có thể sử dụng để truy cập vào tài khoản người dùng thông qua API.
	+ curl -X POST -H "Authorization: Bearer ACCESS_TOKEN""https://API_SERVER.DOMAIN/v2/$OBJECT"

## Refresh Token Flow
- Nếu **access_token** hết hạn, thì khi sử dụng để gọi API sẽ gặp thông báo lỗi "Invalid Token Error". Đừng lo, nếu Service hỗ trợ cơ chế **refresh token** ta có thể sử dụng để yêu cầu **access_token** mới.
	+ https://OAUTH_SERVER.DOMAIN/oauth/token?grant_type=refresh_token&client_id=CLIENT_ID&client_secret=CLIENT_SECRET&refresh_token=REFRESH_TOKEN

# Login with Facebook
- Facebook SDK dành cho Android cho phép mọi người đăng nhâp vào ứng dụng bằng tài khoản Facebook.
- Làm theo các bước duới đây để thêm Facebook Login vào ứng dụng của bạn:
- Link: <a href="https://developers.facebook.com/docs/facebook-login/android?sdk=maven">Facebook Login</a>
  
1. Chọn ứng dụng hoặc tạo ứng dụng mới. Bạn phải có ứng dụng trên <a href="https://developers.facebook.com/apps/482332635894680/dashboard/">facebook for developers</a>
<img src="images/choose_app_st1.png"/>

2. Tải xuống ứng dụng Facebook nếu chưa có: <a role="button" class="_42ft _3g_o _3g_p _3g_u" href="https://play.google.com/store/apps/details?id=com.facebook.katana&amp;fbclid=IwAR2sahItcGmas46TkPcG47R38gnLx860GRQPM2ZW2GYSTi3iM50KyeZEwuM" target="_blank" rel="nofollow" data-lynx-mode="asynclazy" data-lynx-uri="https://l.facebook.com/l.php?u=https%3A%2F%2Fplay.google.com%2Fstore%2Fapps%2Fdetails%3Fid%3Dcom.facebook.katana%26fbclid%3DIwAR2sahItcGmas46TkPcG47R38gnLx860GRQPM2ZW2GYSTi3iM50KyeZEwuM&amp;h=AT0q1EE4F3Qa3TnATRjFCsGKeG1GiyqrANXN-mz-yJsDpBlgPqQZKKB4VOvdbP4o0MFidckGKowhT1vGWc6KycApb9hC8hCkUa47Y5tcf-R35JT3OfAXde2cXx77EQ">Tải xuống Facebook dành cho Android</a>

3. Tích hợp Facebook SDK
- Facebook Login SDK dành cho Android là một thành phần của <a href="https://developers.facebook.com/docs/android/componentsdks">SDK Facebook dành cho Android</a>. Để sử dụng trong project hãy biến SDK này thành phụ thuộc trong Maven hoặc tải xuống. Ở đây mình sẽ thêm phụ thuộc vào Maven.

  a. Trong build.gradle(project), đảm bảo kho lưu trữ **jcenter()** được liệt kê trong **buildscript { repositories {}}**
  
  b. Trong build.gradle(Module:app) thêm thư viện: implementation 'com.facebook.android:facebook-login:5.4.0
  
  c. Khi sử dụng Facebook Login SDK, sự kiện trong ứng dụng của bạn sẽ tự động được ghi lại và thu thập lại. Bạn có thể vô hiệu hóa tính năng này. Tham khảo: <a href="/docs/app-events/automatic-event-collection-detail">Ghi sự kiện trong ứng dụng tự động</a>
 
4. Chỉnh sửa resource và tệp manifest
- Tạo chuỗi cho ID ứng dụng Facebook và các chuỗi cần để bật Tab tùy chỉnh của Chrome. Thêm FacebookActivity vào Manifest.xml
  a. Thêm phần sau vào file strings.xml
   
  b. Thêm quyền INTERNET.
    
  c. Thêm thành phần meta-data, FacebookActivity.
  
5. Liên kết package name và default class vaof ứng dụng.
<img src="images/connect_package_name.png"/>

6. Cung cấp **Key hash** cho ứng dụng.
- Để đảm bảo tính xác thực giữa ứng dụng và Facebook, cần cung cấp **key hash** Android cho môi trường phát triển của mình.
- Bạn sẽ có duy nhất một **Develop Key Hash** cho mỗi môi trường phát triển Android.
- Để tạo **Key dev** đối với Linux sử dụng lệnh dưới, những nền tảng khác xem <a href="https://medium.com/mindorks/generate-hash-key-for-facebook-and-sha-1-key-for-google-maps-in-android-studio-48d92e4f3c05" rel="nofollow">tại đây</a>
  + keytool -exportcert -alias androiddebugkey -keystore debug.keystore | openssl sha1 -binary | openssl base64
  
- Khóa tạo ra là một chuỗi gồm 28 ký tự, sau đó sao chép và điền vào trường Key Hash:
<img src="images/key_hash.png"/>
7. Bật đăng nhập một lần cho ứng dụng
<img src="images/single_sign_on.png"/>

8. Thêm nút Đăng nhập Facebook.
- Cách đơn giản nhất để thêm là sử dụng **LoginButton** từ SDK. Đây là thành phần giao diện người dùng bao phủ chức năng có trong **LoginManager**.

9. Đăng ký gọi lại
- Tạo một đối tượng callbackManager để nhận các phản hồi đăng nhập:
<img src="images/callback_manager.png"/>

- Thêm các quyền cần thiết khi tiến hành Login với tài khoản Facebook. Tham khảo <a href="https://developers.facebook.com/docs/facebook-login/android/permissions">tại đây</a>
<img src="images/permissions.png"/>

<img src="images/login_permission.png"/>

- Đăng ký callback để nhận trạng thái đăng nhập:
  + Nếu sử dụng **LoginButton**:
<img src="images/register_button_facebook.png"/>

  + Nếu sử dụng Custom button thì sử dụng **LoginManager**

<img src="images/register_custom_button.png"/>
  
- Chuyển kết quả đăng nhập đến LoginManager thông qua callbackManager:
<img src="images/onActivityResult.png"/>

- Lấy thông tin user thông qua đối tượng Profile:
<pre><code>val user = Profile.getCurrentProfile()
</code></pre>

10. Kiểm tra trạng thái đăng nhập
- Ứng dụng của bạn chỉ có thể có một người đăng nhập mỗi lần. Facebook SDK sẽ lưu trữ dữ liệu này, bạn có thể kiểm tra xem người dùng đã đăng nhập chưa bằng cách sau:
<img src="images/login_fb_status.png"/>

## Theo dõi Access Token và Profile
- Nếu muốn ứng dụng cập nhật **access_token** và trang cá nhân hiện tại, bạn có thể triển khai lớp **AccessTokenTracker** và **ProfileTracker**.
- Các class này sẽ lắng nghe khi có sự thay đổi, sử dụng bộ thu nội bộ nên bạn cần gọi stopTracking() trong onDestroy().
- Theo dõi **access_token**:

<img src="images/tracking_token.png"/>

- Theo dõi trang cá nhân:

<img src="images/tracking_profile.png"/>


# Login with Google
- Trước khi tích hợp Google Sign-in trong ứng dụng, cần phải configure Google API Console và thiết lập Android Studio.
- Tham khảo: <a href="https://developers.google.com/identity/sign-in/android/start-integrating">Google Sign-in</a>

## Yêu cầu
- Thiết bị phải chạy Android 4.1 trở lên có Google Play Store hoặc Emulator có AVD chạy Google APIs dựa trên Android 4.2.2 trở lên, có Google Play Service version 15.0.0 trở lên.
- Version mới nhất của Android SDK, bao gồm SDK Tools.
- Project cấu hình từ Android 4.1 trở lên.
- Google Play Service: Tools -> SDK Manager -> SDK Tools -> Google Repository

## Tích hợp Google Sign-In vào ứng dụng
### Add Google Play Service.
- Trong top-level build.gradle:
<img src="images/google_repo.png"/>

- Trong app-level build.gradle:
<img src="images/dependencies.png"/>

### Cấu hình Google API Console.
<img src="images/configure_1.png"/>

<img src="images/configure_2.png"/>

- Cần cung cấp mã SHA-1 để tạo OAuth2 client và API key cho ứng dụng. Để tạo SHA-1 xem <a href="https://developers.google.com/android/guides/client-auth">
Authenticating Your Client</a>
<img src="images/configure_3.png"/>

- Kết quả configure: <a href="https://console.developers.google.com/apis/credentials?authuser=0&project=integrate-login-1566443852554&folder&organizationId=810507265186">API Console</a>
<img src="images/result_configure.png"/>

- Nếu ứng dụng của bạn sử dụng <a href="https://developers.google.com/identity/sign-in/android/backend-auth">authenticates with a backend server</a> hoặc <a href="https://developers.google.com/identity/sign-in/android/offline-access">accesses Google APIs from your backend server</a>, bạn phải lấy **OAuth2 client ID** đã được tạo trong server của bạn.
	+ Mở <a href="https://console.developers.google.com/apis/credentials?project=integrate-login-1566443852554&folder&organizationId=810507265186">Credentials page</a> trong API Console
	+ Web application type client ID là OAuth2 client ID

<img src="images/oauth_client_id.png"/>

- Thêm **client ID** này vào **requestIdToken** hoặc **requestServerAuthCode** khi tạo đối tượng **GoogleSignInOptions**

### Add Sign-In
1. Configure Google Sign-in and the GoogleSignInClient object
- Trong method onCreate(), tạo đối tượng **GoogleSignInOptions**:
<img src="images/google_sign_in_options.png"/>

- Có thể thêm scope để truy cập Google APIs bằng cách thêm **requestScopes**. Xem thêm <a href="https://developers.google.com/identity/sign-in/android/additional-scopes">Requesting Additional Scopes.</a>
- Sau đó tạo đối tượng **GoogleSignInClient** :
<img src="images/google_sign_in_client.png"/>

2. Kiểm tra đăng nhập
- Trong method onStart(), bạn có thể kiểm tra xem user đã đăng nhập bằng Google chưa:
<img src="images/check_login_status.png"/>

3. Add Google Sign-in button.
- Thêm **SignInButton** vào layout:
<img src="images/sign_in_button.png"/>

- Nếu sử dụng sign-in button mặc định, có thể tùy chỉnh kích thước button:
<img src="images/set_size_button.png"/>

4. Bắt đầu luồng đăng nhập
- Mở màn hình chọn tài khoản đăng nhập thông qua **mGoogleSignInClient.signInIntent**
<img src="images/start_flow_sign_in.png"/>

- Sau khi user đăng nhập, đối tượng **GoogleSignInAccount** được trả về trong onActivityResult(), đối tượng này chứa thông tin user.
<img src="images/google_sign_in_account.png"/>

<img src="images/handle_sign_in_result.png"/>

<img src="images/google_lg_fl.png" width='400' height='800'/>

5. Lấy thông tin User
<img src="images/get_user_profile.png"/>

6. Đăng xuất
- Sử dụng method **signOut**:
<img src="images/sign_out_gg.png"/>

7. Disconnect account
- Bạn nên cung cấp cho user đã đăng nhập bằng Google khả năng ngắt kết nối tài khoản Google khỏi ứng dụng. Nếu user xóa tài khoản của họ, bạn phải xóa thông tin mà ứng dụng thu thập được từ Google API. Bằng cách sử dụng method **revokeAccess**
<img src="images/disconnect_account.png"/>

### Xác thực với backend server
<a href="https://developers.google.com/identity/sign-in/android/backend-auth">Authenticate with a backend server</a>

### Cho phép quyền truy cập Server-Side
<a href="https://developers.google.com/identity/sign-in/android/offline-access">Enabling Server-Side Access</a>

# Login with Twitter
- Để sử dụng tính năng này, đảm bảo rằng đã đăng nhập vào Twitter.
1. Tạo một App trong <a href="https://developer.twitter.com/en/apps">liên kết này</a>.
- Điền đầy đủ thông tin vào form **Application Details**, Nếu muốn xác thực qua Twitter thì trường **Callback URLs** điền **twittersdk://**
2. Sau khi tạo xong, bạn sẽ nhận được API Key và API Secret. Sao chép cả 2 key này vào trong file **string.xml**
<img src="images/created_app_twitter.png"/>

3. Add dependencies. <a href="https://github.com/twitter-archive/twitter-kit-android/wiki">Read more about Twitter APIs</a>
<img src="images/dependencies_tw.png"/>

4. Add **TwitterLoginButton** trong layout
- Ta có thể sử dụng default button hoặc có thể custom.
<img src="images/tw_button_login.png"/>

5. Cấu hình Twitter để sử dụng trong ứng dụng
- Tạo Twitter Kit:
	+ Cấu hình mặc định or cấu hình tùy chỉnh thông qua đối tượng **TwitterConfig**
<img src="images/twitter_kit.png"/>

6. Thực hiện luồng login
- Tạo một callback để nhận kết quả login trả về:
	+ Default button
<img src="images/callback_btn_default.png"/>
	
	+ Custom button:
<img src="images/callback_custom_button.png"/>

-  Nhận dữ liệu trả về trong **onActivityResult()**:
<img src="images/tw_onActivityResult.png"/>

<img src="images/tw_lg_fl.png" width='400' height='800'/>

7. Lấy dữ liệu khi đã login thành công thông qua đối tượng **TwitterSession**
<img src="images/get_user_tw.png"/>

8. Kiểm tra đăng nhập
- Để check xem User đã login chưa, sử dụng **TwitterCore** để check xem có **activeSession** không, và có **token** trả về hay không
<img src="images/check_login_tw.png"/>

9. Đăng xuất
<img src="images/tw_logout.png"/>

# Tài liệu tham khảo
- Facebook: https://developers.facebook.com/docs/facebook-login/android
- Google: https://developers.google.com/identity/sign-in/android/start-integrating
- Twitter: https://github.com/twitter-archive/twitter-kit-android/wiki
