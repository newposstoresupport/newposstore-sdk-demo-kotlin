# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Param V2 demo：查询 / 下载 / 一键下载（对齐 StoreSdk api `1.0.3`）
- Firmware OTA demo：查询 / 下载固件
- `app/libs/sdk.jar`（ROM `DevConfig` / `SDKManager`，`compileOnly`）
- `local.properties` 提供 demo 测试凭据（随仓库提交，供客户直接运行）

### Changed
- Maven 依赖 `api` 升级至 `1.0.3`
- 凭据改为从 `local.properties` / 环境变量读取，写入 `BuildConfig`，不再硬编码在 `MainApplication`
- README Maven 示例版本更新为 `api:1.0.3`
