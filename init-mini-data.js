const mysql = require('mysql2/promise');

const dbConfig = {
  host: 'localhost',
  port: 3306,
  user: 'root',
  password: 'root',
  database: 'edu_system',
  waitForConnections: true,
  connectionLimit: 10,
};

async function main() {
  console.log('=== 初始化小程序家长端测试数据 ===\n');
  
  const connection = await mysql.createConnection(dbConfig);
  console.log('✅ 数据库连接成功');

  try {
    // 获取家长ID
    const [parentRows] = await connection.execute(
      'SELECT id, username FROM sys_user WHERE username IN (?, ?)',
      ['parent01', 'parent02']
    );
    let parent01Id, parent02Id;
    
    const passwordHash = '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2';
    if (!parentRows.find(p => p.username === 'parent01')) {
      const [r] = await connection.execute(
        `INSERT INTO sys_user (username, password, real_name, nick_name, mobile, gender, status, create_time, update_time)
         VALUES (?, ?, ?, ?, ?, 1, 1, NOW(), NOW())`,
        ['parent01', passwordHash, '张爸爸', '张爸爸', '13800138001']
      );
      parent01Id = r.insertId;
      await connection.execute('INSERT INTO sys_user_role (user_id, role_id) VALUES (?, 4)', [parent01Id]);
      console.log('✅ 创建家长parent01, ID:', parent01Id);
    } else {
      parent01Id = parentRows.find(p => p.username === 'parent01').id;
    }
    if (!parentRows.find(p => p.username === 'parent02')) {
      const [r] = await connection.execute(
        `INSERT INTO sys_user (username, password, real_name, nick_name, mobile, gender, status, create_time, update_time)
         VALUES (?, ?, ?, ?, ?, 2, 1, NOW(), NOW())`,
        ['parent02', passwordHash, '李妈妈', '李妈妈', '13800138002']
      );
      parent02Id = r.insertId;
      await connection.execute('INSERT INTO sys_user_role (user_id, role_id) VALUES (?, 4)', [parent02Id]);
      console.log('✅ 创建家长parent02, ID:', parent02Id);
    } else {
      parent02Id = parentRows.find(p => p.username === 'parent02').id;
    }
    console.log('parent01 ID:', parent01Id, ', parent02 ID:', parent02Id);

    // 获取在读学生
    const [students] = await connection.execute(
      "SELECT id, name, mobile, status FROM edu_student WHERE status = 'ENROLLED' LIMIT 5"
    );
    console.log('\n在读学生:');
    students.forEach(s => console.log(`  - ${s.name} (ID:${s.id}, mobile:${s.mobile || 'N/A'})`));

    const s1 = students[0];
    const s2 = students[1];
    const s3 = students[2] || students[0];

    // 建立亲子关系 - 填充所有必填字段
    console.log('\n=== 建立亲子关系 ===');
    const relations = [
      { userId: parent01Id, student: s1, relation: '父亲', parentName: '张爸爸', parentMobile: '13800138001', sort: 1 },
      { userId: parent01Id, student: s2, relation: '父亲', parentName: '张爸爸', parentMobile: '13800138001', sort: 2 },
      { userId: parent02Id, student: s3, relation: '母亲', parentName: '李妈妈', parentMobile: '13800138002', sort: 1 },
    ];

    for (const rel of relations) {
      const [existing] = await connection.execute(
        'SELECT id FROM edu_student_parent WHERE user_id = ? AND student_id = ?',
        [rel.userId, rel.student.id]
      );
      if (existing.length === 0) {
        await connection.execute(
          `INSERT INTO edu_student_parent (student_id, user_id, name, relation, mobile, sort, deleted, create_time)
           VALUES (?, ?, ?, ?, ?, ?, 0, NOW())`,
          [rel.student.id, rel.userId, rel.parentName, rel.relation, rel.parentMobile, rel.sort]
        );
        console.log(`  ✅ ${rel.parentName} -> ${rel.student.name} (${rel.relation})`);
      } else {
        console.log(`  亲子关系已存在: ${rel.parentName} -> ${rel.student.name}`);
      }
    }

    // 检查学生是否有班级
    console.log('\n=== 检查学生班级 ===');
    for (const student of [s1, s2, s3]) {
      const [cs] = await connection.execute(
        `SELECT cs.*, c.name as className 
         FROM edu_class_student cs 
         LEFT JOIN edu_class c ON cs.class_id = c.id 
         WHERE cs.student_id = ? AND cs.deleted = 0`,
        [student.id]
      );
      if (cs.length > 0) {
        console.log(`  ${student.name} 已在班级: ${cs[0].className || 'ID:' + cs[0].class_id}`);
      } else {
        console.log(`  ⚠️  ${student.name} 未分配班级`);
        const [classes] = await connection.execute('SELECT id, name FROM edu_class WHERE deleted = 0 LIMIT 1');
        if (classes.length > 0) {
          await connection.execute(
            `INSERT INTO edu_class_student (class_id, student_id, status, enroll_date, create_time)
             VALUES (?, ?, 'ENROLLED', CURDATE(), NOW())`,
            [classes[0].id, student.id]
          );
          console.log(`    ✅ 已分配到班级: ${classes[0].name}`);
        }
      }
    }

    // 给家长角色添加必要权限
    console.log('\n=== 检查家长权限 ===');
    const [permCheck] = await connection.execute(
      `SELECT rm.id FROM sys_role_menu rm
       JOIN sys_menu m ON rm.menu_id = m.id
       WHERE rm.role_id = 4 AND m.perms = 'student:parent:query'`
    );
    if (permCheck.length === 0) {
      // 查找家长API相关菜单或创建权限
      console.log('  ⚠️  家长角色缺少student:parent:query权限');
      // 直接给家长角色分配所有以student:parent开头的权限对应的菜单
      const [parentMenus] = await connection.execute(
        "SELECT id FROM sys_menu WHERE perms LIKE 'student:parent%' OR perms LIKE 'teacher:dashboard%'"
      );
      console.log('  找到相关菜单:', parentMenus.length);
      for (const menu of parentMenus) {
        await connection.execute(
          'INSERT IGNORE INTO sys_role_menu (role_id, menu_id) VALUES (?, ?)',
          [4, menu.id]
        );
      }
      // 同时给家长角色分配teacher dashboard权限（如果是教师角色）
      console.log('  ✅ 权限已添加');
    } else {
      console.log('  ✅ 家长角色已有必要权限');
    }

    // 验证parent01登录
    console.log('\n=== 验证家长登录API ===');
    const http = require('http');
    const loginRes = await new Promise((resolve, reject) => {
      const req = http.request({
        hostname: 'localhost',
        port: 8080,
        path: '/api/v1/auth/login',
        method: 'POST',
        headers: { 'Content-Type': 'application/json' }
      }, (res) => {
        let body = '';
        res.on('data', chunk => body += chunk);
        res.on('end', () => resolve(JSON.parse(body)));
      });
      req.on('error', reject);
      req.write(JSON.stringify({ username: 'parent01', password: '123456' }));
      req.end();
    });
    if (loginRes.code === 200) {
      console.log('  ✅ parent01登录成功!');
      console.log('    用户:', loginRes.data.realName);
      
      // 测试获取孩子列表
      const childrenRes = await new Promise((resolve, reject) => {
        const req = http.request({
          hostname: 'localhost',
          port: 8080,
          path: '/api/v1/parent/children',
          method: 'GET',
          headers: { 'Authorization': `Bearer ${loginRes.data.accessToken}` }
        }, (res) => {
          let body = '';
          res.on('data', chunk => body += chunk);
          res.on('end', () => resolve(JSON.parse(body)));
        });
        req.on('error', reject);
        req.end();
      });
      if (childrenRes.code === 200) {
        console.log('  ✅ 获取孩子列表成功, 数量:', childrenRes.data.length);
        childrenRes.data.forEach(c => console.log(`    - ${c.name} (${c.grade})`));
      } else {
        console.log('  ❌ 获取孩子列表失败:', childrenRes.message);
      }
    } else {
      console.log('  ❌ parent01登录失败:', loginRes.message);
    }

    console.log('\n=== 数据初始化完成 ===');
    console.log('可用账号:');
    console.log('  教师端: teacher01/123456 (张老师), teacher02/123456 (李老师)');
    console.log('  家长端: parent01/123456 (张爸爸), parent02/123456 (李妈妈)');
    console.log('  管理员: admin/123456');

  } catch (error) {
    console.error('❌ 错误:', error.message);
    console.error(error.stack);
  } finally {
    await connection.end();
  }
}

main();
