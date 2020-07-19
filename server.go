package main

import(
  "fmt"
  //"os"
  "encoding/json"
  "context"
  "time"
  "log"
  "github.com/satori/go.uuid"
  "go.mongodb.org/mongo-driver/mongo"
  "go.mongodb.org/mongo-driver/bson"
  "go.mongodb.org/mongo-driver/mongo/options"
  "github.com/kataras/iris/v12"
  "github.com/kataras/iris/v12/middleware/logger"
  //"github.com/kataras/iris/v12/sessions"
  //"github.com/kataras/iris/v12/sessions/sessiondb/redis"
)

type picture struct{
  A        string `json:"a"`
  Urls   []string `json:"urls"`
  Nums     int    `json:"nums"`
}

type shop struct{
  Username    string
  ShopName    string
  IntroImage  string
  Vegetables  []vegetable
  More        string
}

type vegetable struct{
  Price       int
  Image       string
  Name        string
  More        string
}

type user struct{
  Name        string
  Pass        string
  Phone       string
  Addr        string
  Shop        string
  Order       string
  More        string
}

func Page(database *mongo.Database,collection string,limit int64,page int64) (data []map[string]interface{},err error){
  ctx, cannel := context.WithTimeout(context.Background(), time.Minute)
  defer cannel()
  findoptions:=options.Find()
  if limit > 0 {
    findoptions.SetLimit(limit)
    findoptions.SetSkip(limit * page)
  }
  cur, err := database.Collection(collection).Find(ctx, bson.M{}, findoptions)
  if err != nil {
    return nil, err
  }
  defer cur.Close(context.Background())
  err = cur.All(context.Background(), &data)
  fmt.Println(len(data))
  return
}

func find(database *mongo.Database,collection string,doc bson.D) bool{
  cur, err := database.Collection(collection).Find(context.TODO(), doc)
  if err != nil {
    return true
  }
  var data []map[string]interface{}
  cur.All(context.Background(), &data)
  if len(data)>0{
    return true
  }
  fmt.Println(data)
  return false
}

func insert(database *mongo.Database,collection string,doc bson.D) bool{
  _,err:=database.Collection(collection).InsertOne(context.TODO(),doc)
  if err != nil {
      return false
  }

  return true
}

func Update(database *mongo.Database,collection string,filter interface{},update interface{}) bool{
  _,err:=database.Collection(collection).UpdateOne(context.TODO(), filter, update)
  if err != nil {
    fmt.Println(err)
      return false
  }

  return true
}
/*
func ReadAll(filePth string) ([]byte, error) {
 f, err := os.Open(filePth)
 if err != nil {
  return nil, err
 }
 
 return ioutil.ReadAll(f)
}
*/
func main(){

  clientOptions := options.Client().ApplyURI("mongodb://localhost:27017")

  // 连接到MongoDB
  client, err := mongo.Connect(context.TODO(), clientOptions)
  if err != nil {
    log.Fatal(err)
  }
  defer client.Disconnect(context.TODO())

  // 检查连接
  err = client.Ping(context.TODO(), nil)
  if err != nil {
    log.Fatal(err)
  }
/*
  db := redis.New(redis.Config{
    Network:   "tcp",
    Addr:      "127.0.0.1:6379",
    Timeout:   time.Duration(30) * time.Second,
    MaxActive: 10,
    Password:  "",
    Database:  "",
    Prefix:    "",
    Delim:     "-",
    Driver:    redis.Redigo(), // redis.Radix() can be used instead.
  })

  iris.RegisterOnInterrupt(func() {// 处理Ctrl C
    client.Disconnect(context.TODO())
    db.Close()
  })

  defer db.Close()

  sess := sessions.New(sessions.Config{
    Cookie:       "sessionscookieid",
    Expires:      45 * time.Minute,
    AllowReclaim: true,
  })

  sess.UseDatabase(db)
*/
  app:=iris.New()//                                          iris app
  
  customLogger := logger.New(logger.Config{
    // Status displays status code
    Status: true,
    // IP displays request's remote address
    IP: true,
    // Method displays the http method
    Method: true,
    // Path displays the request path
    Path: true,
    // Query appends the url query to the Path.
    Query: true,

    // Columns: true,

    // if !empty then its contents derives from `ctx.Values().Get("logger_message")
    // will be added to the logs.
    MessageContextKeys: []string{"logger_message"},

    // if !empty then its contents derives from `ctx.GetHeader("User-Agent")
    MessageHeaderKeys: []string{"User-Agent"},
  })

  app.Use(customLogger)

  app.HandleDir("/shopimage", "./assets")
  app.HandleDir("/start","./start")
  app.HandleDir("/release","./release")

//                                                                   start
  start:=app.Party("/start")
  start.Get("/",func (ctx iris.Context){
    ctx.WriteString("abc")
    })

//                                                                   annoce
  annoce:=app.Party("/annoce")
  annoce.Get("/",func (ctx iris.Context){
    ctx.WriteString("公告")
    })

//                                                                   shop
  shop:=app.Party("/shop")
  shop.Get("/show",func(ctx iris.Context){
    var js picture
    var path="http://"+ctx.Host()
    js=picture{A:"abc",Urls:[]string{path+"/shopimage/c9.jpg",path+"/shopimage/c10.jpg",path+"/shopimage/c11.jpg"},Nums:3}
    j,_:=json.Marshal(js)
    ctx.WriteString(string(j))
  })
  shop.Get("/shoplist/{position:uint64}",func(ctx iris.Context){
      pos,_:=ctx.Params().GetInt64("position")
      shoplist,_:=Page(client.Database("shops"),"shop",3,pos)
      j,_:=json.Marshal(shoplist)
      ctx.WriteString(string(j))
    })
  shop.Get("/store/{shopname:string}",func(ctx iris.Context){
    shopname:=ctx.Params().Get("shopname")
    cur, err := client.Database("veges").Collection("vege").Find(context.TODO(), bson.D{{"name",shopname}})
    if err != nil {
      fmt.Println("error")
    }
    var data []map[string]interface{}
    cur.All(context.Background(), &data)
    j,_:=json.Marshal(data[0]["veges"])
    ctx.WriteString(string(j))
    })

//                                                                   login
  login:=app.Party("/login")
  login.Post("/sigin",func(ctx iris.Context){
    
    result:=find(client.Database("users"),"user",bson.D{{"name",ctx.FormValue("name")},{"pass",ctx.FormValue("pass")}})
    if result{
        ctx.WriteString("ok")
      }else{
        ctx.WriteString("no")
        return
      }
    
    })
  login.Get("/logout", func(ctx iris.Context){
    
    })
  login.Post("/registe",func(ctx iris.Context){
    result:=insert(client.Database("users"),"user",bson.D{{"name",ctx.FormValue("name")},{"pass",ctx.FormValue("pass")}})
    if result{
      ctx.WriteString("ok")
    }else{
      ctx.WriteString("no")
    }
    })
  login.Post("/third",func(ctx iris.Context){
    
    })
  login.Post("/check",func(ctx iris.Context){
    //fmt.Println(ctx.FormValue("name"))
    result:=find(client.Database("users"),"user",bson.D{{"name",ctx.FormValue("name")}})
    if result{
      ctx.WriteString("ok")
    }else{
      ctx.WriteString("no")
    }
    })

  go func() {
        fmt.Println(1)
        fmt.Println(uuid.NewV4())
    }()

  app.Run(iris.Addr(":8000"))
}
