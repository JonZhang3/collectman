var producer = kafka.producer({});
var test = "123";
producer.send({"A": test});

// const db = database.connect({
//     url: 'jdbc:oracle:thin:@130.10.7.211:1521:orcl',
//     username: 'lgszxt',
//     password: 'lgszxt',
//     maxActive: 10,
//     initialSize: 5
// });
// const lasttime = "";
// stask.addTask({
//     fixedRate: 3000
// }, function() {
//     // var dataList = db.query('');
//     print(db.query(`SELECT COUNT(1) FROM qxpt_ryxx`));
// });

