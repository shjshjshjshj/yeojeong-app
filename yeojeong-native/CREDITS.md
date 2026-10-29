# Credits

## App assets

- App icon, onboarding images, and copy: project-owned assets.
- Subway station data: `line*.json`, `subline1.json`, `subwayPos.json`, normalized for this app.
- Seoul metropolitan connection graph: `capitalStations.json` from iml1111/Korea-Subway (MIT License), used for station adjacency and coordinates.

## Public data

- Place data: Korea Tourism Organization TourAPI (`KorService2/locationBasedList2`, `searchKeyword2`), accessed through data.go.kr.
- Weather: Korea Meteorological Administration ultra-short forecast API, with Open-Meteo as a fallback when the KMA key is unavailable or expired.
- The cached seed files `tour_seed.json` and `tour_cafe_seed.json` are snapshots of TourAPI responses and should be refreshed before release.

## Photos

Demo photos are from Unsplash and used under the Unsplash License.

| File | Source |
|---|---|
| `spot_forest.jpg` | https://images.unsplash.com/photo-1441974231531-c6227db76b6e |
| `spot_cafe.jpg` | https://images.unsplash.com/photo-1495474472287-4d71bcdd2085 |
| `spot_river.jpg` | https://images.unsplash.com/photo-1506744038136-46273834b3fb |
| `spot_city.jpg` | https://images.unsplash.com/photo-1519501025264-65ba15a82390 |
| `spot_park.jpg` | https://images.unsplash.com/photo-1519331379826-f10be5486c6f |
| `spot_flowers.jpg` | https://images.unsplash.com/photo-1522383225653-ed111181a951 |

운영 배포 전에는 각 장소의 공식 사진 또는 직접 촬영한 사진으로 교체하는 것을 권장합니다.
