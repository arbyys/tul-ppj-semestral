# PPJ - Semestrální práce 

Cílem projektu je vytvořit aplikaci pro ukládání a zobrazování meteorologických dat.

## Požadavky na technické řešení
1. Maven pro sestavení
2. Spring (Boot) jako implementační framework
3. Verzování na GitHubu

## Datový model (persistence)
- Stát (MySQL) - soubor Country.java
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 100)
  private String name;

  @Column(nullable = true, length = 3)
  private String isoCode;

  @Column(nullable = true)
  private Long population;

  @Column(nullable = true)
  private String continent;
-  Město (MySQL) - soubor City.java
-     @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 100)
  private String name;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "country_id", nullable = false)
  private Country country;

  @Column(nullable = true)
  private Long population;

  @Column(nullable = true, precision = 10, scale = 6)
  private Double latitude;

  @Column(nullable = true, precision = 10, scale = 6)
  private Double longitude;

  @OneToMany(mappedBy = "city")
  private List<Record> records;
-  Měření pro město (MySQL) - soubor Record.java
-     @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private double min_temperature;
  private double max_temperature;
  private int pressure;
  private int humidity;
  private double wind_speed;
  private int wind_deg;

  private LocalDateTime timestamp;

  @ManyToOne
  private City city;

## API
Aplikace bude poskytovat REST API pro přímou komunikaci.

## REST 
Aplikace bude obsahovat REST rozhraní pro přidávání, editaci a mazání států, měst a měření. A dále zobrazení aktuálních hodnot a průměru za poslední den, týden a 14 dní.

## Testování
Součástí řešení budou testy pro všechny operace volané přes REST API.

## Konfigurace
Musí být možno provádět externí konfiguraci – tj. veškerá konfigurace do properties souborů.

## Logování
Aplikace by měla využívat logovací systém Logback s výpisem do souboru (např. log.out). V případě chyby Vám bude zaslán pouze soubor log.out – výstup z konzole pouze v případě, že neprojdou testy.

## Data
Data je možné získávat z libovolného veřejně dostupného API, například http://www.openweathermap.com - s bezplatným přístupem při dodržení limitu 60 volání za sekundu.