# Wanted

手配度プラグインです。  
手配度に応じてプレイヤーの周りにMobがスポーンします。  
主にplugins/Wanted/config.ymlを編集して利用します。

## 動作環境
- Minecraft 1.15.2
- PaperMC 1.15.2

## コマンド
wanted.wanted権限が必要です。（default: op）
- /wanted v [start|end] 投票の開始（投票数が最も多いプレイヤーを手配度MAXにします）
- /wanted [player] [level] プレイヤーの手配度を指定
- /wanted reload config 設定を再読み込み

## 設定
Mobはoffsetからoffset+rangeの間にスポーンします。  
式には四則演算と累乗、括弧を使うことができます。（+ | - | * | / | ^)
|設定名|値|説明|
|-|-|-|
|max-level|整数|手配度の最大値|
|level|少数|各種違法行為の上昇値|
|words|文字列|違法な発言一覧|
|items|アイテム|違法アイテム一覧|
|wanted-time|秒|指名手配時間|
|spawn|真偽値|Mobのスポーン設定|
|mobs|Mob|昼間、夜間に湧くMob一覧|
|range|整数|Mobがスポーンする範囲|
|offset|整数|スポーン範囲のオフセット|
|limit|整数|範囲内のMob最大数|
|interval|式|Mobがスポーンする頻度|
|amount|式|Mobがスポーンする数|