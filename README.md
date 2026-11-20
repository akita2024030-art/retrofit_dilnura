# Laboratory 10: Retrofit

## Overview
This application demonstrates network requests using **Retrofit**, **Gson**, **OkHttp Logging Interceptor**, and **Coil** in Jetpack Compose, following the requirements of Laboratory 10.

---

## How a Call Travels from Interface to Server

When a suspend function in `GitHubService` (e.g., `users()`) is invoked, the call traverses the following architecture layers:

1. **Retrofit & Dynamic Proxy**:
   - Retrofit uses Java Dynamic Proxy (`Proxy.newProxyInstance`) to implement `GitHubService`.
   - When `service.users()` is called, Retrofit inspects the annotations (`@GET`, `@Query`, `@Path`) and serializes parameters into HTTP requests.

2. **OkHttp Client & Interceptors**:
   - Retrofit passes the HTTP request to **OkHttp**.
   - OkHttp runs through its chain of interceptors, including the **HttpLoggingInterceptor** (which logs request/response headers and bodies based on build type: `BASIC` in debug, `NONE` in release).
   - OkHttp handles connection pooling, DNS resolution, and TCP/TLS handshakes to execute the request over the network.

3. **Converter Factory (Gson)**:
   - Once the server responds (e.g., HTTP 200 OK with JSON), OkHttp passes the response body back to Retrofit.
   - Retrofit uses **GsonConverterFactory** (backed by Gson) to deserialize the raw JSON stream into Kotlin data model objects (`List<User>`).

4. **Coroutine & ViewModel Flow**:
   - The result is returned as a `Result` / `UiState` wrapped in a `suspend` function and collected in `MainViewModel` via `viewModelScope`, updating `StateFlow` which the Compose UI observes.

---

## Laboratory 10 Requirements Checklist

1. **Dependencies & Permissions**: Added Retrofit, Converter-Gson, Logging-Interceptor, Coil-Compose, and `INTERNET` permission in `AndroidManifest.xml`.
2. **Model (`User`)**: Mapped `avatar_url` using `@SerializedName("avatar_url")` to `avatarUrl`. Ignored unused fields.
3. **Interface (`GitHubService`)**: Defined `@GET("users")` with `per_page` query and `@GET("users/{login}")` with path parameter.
4. **Retrofit Builder**: Configured with `https://api.github.com/` base URL and Gson converter.
5. **ViewModel & State**: `MainViewModel` uses `viewModelScope` to populate `StateFlow<UiState<List<User>>>` observed by `LazyColumn`.
6. **Query & Path**: Used `per_page = 5` to respect rate limits and path parameter for user detail lookup.
7. **UiState**: Implemented `Loading`, `Success`, and `Error` sealed states.
8. **ApiInstance**: Separated Retrofit builder into a singleton `ApiInstance` object.
9. **Logging**: Configured `HttpLoggingInterceptor` with `BASIC` level in debug and `NONE` in release.
10. **Error Handling & Retry**: Wrapped calls in `safeApiCall`, handling `IOException` and `HttpException` (404), with retry count `n` up to 3 before showing "Open Wi-Fi settings".

---

## Demo Video & Walkthrough

<details>
<summary><b>Click to expand Demo Video (Max 40s)</b></summary>

*(Demo video showcasing happy path, network error with retry count up to 3, recovery path, and 404 error testing)*

</details>
