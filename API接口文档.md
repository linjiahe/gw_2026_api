# 官网 API 接口文档

**Base URL**: `http://localhost:9900`

---

## 一、POST `/api/auth/nonce` — 获取nonce

**请求参数 (Body JSON)**：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| address | string | 是 | 以太坊钱包地址 |

**请求示例**：
```json
{
  "address": "0x71C7656EC7ab88b098defB751B7401B5f6d8976F"
}
```

**响应示例**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "nonce": "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
  }
}
```

---

## 二、POST `/api/auth/login` — 签名登录（支持邀请码）

**请求参数 (Body JSON)**：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| address | string | 是 | 以太坊钱包地址 |
| signature | string | 是 | 钱包签名(hex格式) |
| inviteCode | string | 否 | 邀请码（仅首次登录时绑定） |

**请求示例**：
```json
{
  "address": "0x71C7656EC7ab88b098defB751B7401B5f6d8976F",
  "signature": "0x1a2b3c...",
  "inviteCode": "ABCD2345"
}
```

**响应示例**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "isNewUser": true,
    "userId": 1,
    "address": "0x71c7656ec7ab88b098defb751b7401b5f6d8976f",
    "inviteCode": "K3MNPQR7"
  }
}
```

**响应字段说明**：

| 字段 | 类型 | 说明 |
|------|------|------|
| token | string | JWT令牌，后续请求需放入Header |
| isNewUser | boolean | 是否首次登录 |
| userId | int | 用户ID |
| address | string | 钱包地址 |
| inviteCode | string | 用户自己的邀请码 |

---

## 三、GET `/api/user/profile` — 获取用户信息（需登录）

**请求头**：
```
Authorization: Bearer <token>
```

**响应示例**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "address": "0x71c7656ec7ab88b098defb751b7401b5f6d8976f",
    "status": 1,
    "createdAt": "2026-03-30T10:00:00"
  }
}
```

---

## 四、GET `/api/user/team` — 我的团队信息（需登录）

**请求头**：
```
Authorization: Bearer <token>
```

**请求参数**：无

**响应示例**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "addresses": [
      "0xaaa...bbb",
      "0xccc...ddd"
    ],
    "teamCount": 15,
    "directCount": 3,
    "teamNftCount": 42,
    "directNftCount": 10
  }
}
```

**响应字段说明**：

| 字段 | 类型 | 说明 |
|------|------|------|
| addresses | string[] | 直推用户钱包地址列表 |
| teamCount | int | 团队总人数（含多级下级） |
| directCount | int | 直推人数（仅一级） |
| teamNftCount | int | 团队NFT总数量（含预售） |
| directNftCount | int | 直推NFT总数量（含预售） |

---

## 五、GET `/api/user/team/{address}` — 根据地址查询团队信息

**请求头**：无（公开接口）

**路径参数**：

| 参数 | 类型 | 说明 |
|------|------|------|
| address | string | 钱包地址 |

**请求示例**：`GET /api/user/team/0x71C7656EC7ab88b098defB751B7401B5f6d8976F`

**响应**：同接口四

---

## 六、GET `/api/user/{address}/nft` — 根据地址查询NFT和预售详情

**请求头**：无（公开接口）

**路径参数**：

| 参数 | 类型 | 说明 |
|------|------|------|
| address | string | 钱包地址 |

**请求示例**：`GET /api/user/0x71C7656EC7ab88b098defB751B7401B5f6d8976F/nft`

**响应示例**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "nftRecords": [
      {
        "id": 1,
        "nftLevelId": 1,
        "walletAddress": "0x71c7...",
        "amount": 100.00,
        "quantity": 2,
        "status": 0,
        "createdAt": "2026-03-30T12:00:00",
        "updatedAt": "2026-03-30T12:00:00"
      }
    ],
    "presaleRecords": [
      {
        "id": 1,
        "nftLevelId": 2,
        "walletAddress": "0x71c7...",
        "amount": 50.00,
        "quantity": 1,
        "status": 0,
        "createdAt": "2026-03-29T10:00:00",
        "updatedAt": "2026-03-29T10:00:00"
      }
    ]
  }
}
```

---

## 七、GET `/api/user/{address}/subordinates` — 根据地址查询下级详情

**请求头**：无（公开接口）

**路径参数**：

| 参数 | 类型 | 说明 |
|------|------|------|
| address | string | 钱包地址 |

**请求示例**：`GET /api/user/0x71C7656EC7ab88b098defB751B7401B5f6d8976F/subordinates`

**响应示例**：
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 5,
      "walletAddress": "0xaaa...bbb",
      "status": 1,
      "inviteCode": "XYZ12345",
      "invitedBy": 1,
      "createdAt": "2026-03-30T10:00:00",
      "updatedAt": "2026-03-30T10:00:00",
      "lastLoginAt": "2026-03-30T12:00:00"
    },
    {
      "id": 8,
      "walletAddress": "0xccc...ddd",
      "status": 1,
      "inviteCode": "ABC67890",
      "invitedBy": 1,
      "createdAt": "2026-03-30T11:00:00",
      "updatedAt": "2026-03-30T11:00:00",
      "lastLoginAt": null
    }
  ]
}
```

---

## 八、POST `/api/nft/presale/create` — 创建预售记录（需登录）

**请求头**：
```
Authorization: Bearer <token>
```

**请求参数 (Body JSON)**：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| nftLevelId | int | 是 | NFT等级ID |
| quantity | int | 是 | 购买数量 |

**请求示例**：
```json
{
  "nftLevelId": 1,
  "quantity": 2
}
```

**响应示例**：
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

## 九、POST `/api/nft/record/create` — 创建NFT记录（需登录）

**请求头**：
```
Authorization: Bearer <token>
```

**请求参数 (Body JSON)**：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| nftLevelId | int | 是 | NFT等级ID |
| quantity | int | 是 | 购买数量 |

**响应示例**：
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

## 十、GET `/api/nft/presale/address` — 获取随机预售地址

**响应示例**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "address": "0x1234..."
  }
}
```

---

## 十一、GET `/api/nft/address` — 获取随机NFT地址

**响应示例**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "address": "0x5678..."
  }
}
```

---

## 十二、GET `/api/nft/presale/records` — 预售记录全网前20

**响应示例**：
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "nftLevelId": 1,
      "walletAddress": "0xaaa...",
      "amount": 100.00,
      "quantity": 2,
      "status": 0,
      "createdAt": "2026-03-30T12:00:00",
      "updatedAt": "2026-03-30T12:00:00"
    }
  ]
}
```

---

## 十三、GET `/api/nft/records` — NFT记录全网前20

**响应示例**：同接口十二结构

---

## 十四、GET `/api/nft/presale/my` — 我的预售记录（需登录）

**请求头**：
```
Authorization: Bearer <token>
```

**Query参数**：

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| page | long | 1 | 页码 |
| size | long | 10 | 每页条数 |

**请求示例**：`GET /api/nft/presale/my?page=1&size=10`

**响应示例**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [],
    "total": 0,
    "pageNum": 1,
    "pageSize": 10
  }
}
```

---

## 十五、GET `/api/nft/my` — 我的NFT记录（需登录）

**请求头**：
```
Authorization: Bearer <token>
```

**Query参数**：同接口十四

**响应示例**：同接口十四结构

---

## 通用错误响应

```json
{ "code": 401, "message": "邀请码无效", "data": null }
{ "code": 401, "message": "不能使用自己的邀请码", "data": null }
{ "code": 401, "message": "请先获取 nonce", "data": null }
{ "code": 401, "message": "签名验证失败", "data": null }
{ "code": 401, "message": "nonce 已使用，请重新获取", "data": null }
{ "code": 500, "message": "账号已被禁用", "data": null }
```
