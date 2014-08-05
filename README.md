# link-shorten

A link shortener written in Clojure with Postgres. This project creates very short domains using unicode such as foo.com/莆




## Usage

You can start the server by running

    $ lein run

Install the [heroku toolbelt](https://toolbelt.herokuapp.com)
and run


    $ heroku apps:create link-shorten
    Creating link-shorten... done, stack is cedar
    http://link-shorten.herokuapp.com/ | git@heroku.com:link-shorten.git
    Git remote heroku added

Add the domain that Heroku gives you as an environment variable. Do not include the trailing slash

    $ heroku config:add domain=http://link-shorten.herokuapp.com

Add the postgres addon

    $ heroku addons:add heroku-postgresql

You can then deploy with

    $ git push heroku master

## Remote REPL

You can connect to the deployed project via the REPL to debug

    $ heroku config:add user=[...] password=[...]

Then you can launch the REPL:

    $ lein repl :connect http://$REPL_USER:$REPL_PASSWORD@link-shorten.herokuapp.com:80/u/repl


You can skip to the interesting unicode characters by logging into your Postgres database and running

    $ ALTER SEQUENCE links_id_seq RESTART WITH 25000;


## License

Copyright © 2014 Brian Sunter

Distributed under the Eclipse Public License, the same as Clojure.
